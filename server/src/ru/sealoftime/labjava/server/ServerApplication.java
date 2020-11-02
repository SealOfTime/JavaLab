package ru.sealoftime.labjava.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sealoftime.labjava.core.Application;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.EventBus;
import ru.sealoftime.labjava.core.model.RequestExecutor;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.data.concrete.Weapon;
import ru.sealoftime.labjava.core.model.events.*;
import ru.sealoftime.labjava.core.model.io.SpaceMarineCSVFileLoader;
import ru.sealoftime.labjava.core.model.io.SpaceMarineCSVFileUnloader;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.requests.noargs.*;
import ru.sealoftime.labjava.core.model.requests.object.AddRequest;
import ru.sealoftime.labjava.core.model.requests.object.RemoveGreaterRequest;
import ru.sealoftime.labjava.core.model.requests.object.UpdateRequest;
import ru.sealoftime.labjava.core.model.requests.primitives.FilterLessThanWeaponTypeRequest;
import ru.sealoftime.labjava.core.model.requests.primitives.RemoveByIdRequest;
import ru.sealoftime.labjava.core.model.response.InfoResponse;
import ru.sealoftime.labjava.core.model.response.ListResponse;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.model.response.SumOfHealthResponse;
import ru.sealoftime.labjava.core.util.Utf8ResourceBundle;
import ru.sealoftime.labjava.core.view.UserInterface;
import ru.sealoftime.labjava.core.view.cli.*;
import ru.sealoftime.labjava.core.view.cli.commands.ArglessCommand;
import ru.sealoftime.labjava.core.view.cli.commands.ExecuteScriptCommand;
import ru.sealoftime.labjava.core.view.cli.commands.ObjectCommand;
import ru.sealoftime.labjava.core.view.cli.commands.PrintFieldAscedingChapterCommand;
import ru.sealoftime.labjava.server.data.PSQLDataLoader;
import ru.sealoftime.labjava.server.data.PSQLDataUnloader;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

@SuppressWarnings("DuplicatedCode")
public class ServerApplication {
    public static final Logger logger = LogManager.getLogger("Server");
    public static final DatabaseConnectionManager dcm = new DatabaseConnectionManager();
    public static List<RemoteUserInterface> clients = new LinkedList<>();
    public static void main(String[] args) {
        var context = new ApplicationContext();

        var locale = Locale.forLanguageTag("ru-RU");

        var localization = new ServerLocalizationService(locale);
        context.setLocalization(localization);

        var cr  = registerCommands();
        var cli = new CommandLineUserInterface(cr, context){
            @Override
            public void print(String rawOutput, Object... data) {
                logger.info(localization.localize(rawOutput, data));
            }
        };

        var eventBus = new RemoteEventBus();
        context.setEventBus(eventBus);

        var requestExecutor = new RequestExecutor(context);
        context.setRequestExecutor(requestExecutor);

        registerEvents(context,cli);

        var fileName = "./data.csv";
        if(args.length > 0 && args[0] != null && !args[0].isEmpty())
            fileName = args[0];
        else
            logger.error(localization.localize("application.error.no_file_provided"));
        var dataLoader = new PSQLDataLoader(dcm);
        context.setDataLoader(dataLoader);

        var dataProvider = dataLoader.initDataProvider();
        context.setDataProvider(dataProvider);

        var dataUnloader = new PSQLDataUnloader();
        context.setDataUnloader(dataUnloader);

        context.setIsRunning(true);
        try {
            var server = new ServerSocket(42069);
            cli.print("server.open_on_port", server.getLocalPort());
            cli.print("commandline.welcome");

            while(context.getIsRunning()) {
                var connection = awaitConnection(server, cli, context);
            }
            System.exit(0);
        }catch(BindException e){
            logger.error(localization.localize("server.port_occupied"));//todo: logging
        }
        catch(IOException e) {
            logger.error(localization.localize("server.error.unexpected", e.getMessage()));//todo:logging
            System.exit(-1);
        }

//        Runtime.getRuntime().addShutdownHook(new Thread(()->
//        {
//            try {
//                fileUnloader.save(dataProvider);
//            } catch (IOException e) {
//                logger.error(localization.localize("server.error.unexpected", e.getMessage()));//todo: logging
//            }
//        }));

    }

    private static Optional<Socket> awaitConnection(ServerSocket server, CommandLineUserInterface cli, ApplicationContext ctx){
        try {
            var connection = server.accept();
            cli.print("server.incoming_connection", connection.getInetAddress());
            final var ui = new RemoteUserInterface(connection, ctx);
            clients.add(ui);
            new Thread(()->{
                while(ui.isConnected)
                    ui.acceptUserInput();
            }, "Connection: " + connection.getInetAddress().toString() + ":" + connection.getPort())
                    .start();
            return Optional.of(connection);
        } catch (IOException e) {
            logger.error(ctx.getLocalization().localize("server.error.unexpected", e.getMessage()));
        }
        return Optional.empty();
    }

    private static void registerEvents(ApplicationContext ctx, CommandLineUserInterface cli){
        var bus = ctx.getEventBus();

        var handler = new CommandLineEventHandler(cli, ctx);
        bus.subscribe(AddObjectEvent.class, handler::onAdd);
        bus.subscribe(ClearEvent.class, handler::onClear);
        bus.subscribe(HelpEvent.class, handler::onHelp);
        bus.subscribe(InfoEvent.class, handler::onInfo);
        bus.subscribe(RemoveObjectsEvent.class, handler::onRemove);
        bus.subscribe(ShowEvent.class, handler::onShow);
        bus.subscribe(UpdateObjectEvent.class, handler::onUpdate);
        bus.subscribe(ExitEvent.class, handler::onExit);
        bus.subscribe(SumOfHealthEvent.class, handler::onSumOfHealth);
        //bus.subscribe(HistoryEvent.class, handler::onHistory);
        bus.subscribe(PrintEvent.class, handler::onPrint);

        var responseHandler = new CommandLineResponseHandler(cli);
        bus.subscribe(Response.class, responseHandler::onResponse);
        bus.subscribe(ListResponse.class, responseHandler::onResponse);
        bus.subscribe(SumOfHealthResponse.class, responseHandler::onResponse);
        bus.subscribe(InfoResponse.class, responseHandler::onResponse);
    }

    private static CommandRegistry registerCommands()   {
        CommandRegistry cr = new CommandRegistry();
        //TODO: register appropriate response handlers
        cr.register("help", new ArglessCommand(HelpRequest::new));
        cr.register("clear", new ArglessCommand(ClearRequest::new));
        cr.register("exit", new ArglessCommand(ExitRequest::new));
        cr.register("history", new ArglessCommand(HistoryRequest::new));
        cr.register("info", new ArglessCommand(InfoRequest::new));
        cr.register("remove_first", new ArglessCommand(RemoveFirstRequest::new));
        cr.register("save", new ArglessCommand(SaveRequest::new));
        cr.register("sum_of_health", new ArglessCommand(SumOfHealthRequest::new));
        cr.register("show", new ArglessCommand(ShowRequest::new));

        cr.register("execute_script", new ExecuteScriptCommand());
        cr.register("filter_less_than_weapon_type", new Command() {
            @Override
            public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
                if(data.length < 2){
                    tec.print("commandline.request_construct.error.not_enough_arguments");
                    return Optional.empty();
                }
                try {
                    var weaponType = Weapon.valueOf(data[1]);
                    return Optional.of(new FilterLessThanWeaponTypeRequest(weaponType));
                }catch(IllegalArgumentException e){
                    tec.print("commandline.request_construct.error.weapon_invalid");
                    return Optional.empty();
                }
            }
        });
        cr.register("print_field_ascending_chapter", new PrintFieldAscedingChapterCommand());
        cr.register("remove_by_id", new Command() {
            @Override
            public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
                if(data.length < 2){
                    tec.print("commandline.request_construct.error.not_enough_arguments");
                    return Optional.empty();
                }
                try {
                    var id = Integer.parseInt(data[1]);
                    return Optional.of(new RemoveByIdRequest(id));
                }catch(IllegalArgumentException e) {
                    tec.print("commandline.request_construct.error.id_invalid");
                    return Optional.empty();
                }
            }
        });

        cr.register("add", new ObjectCommand() {
            @Override
            public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
                var spaceMarine = constructSpaceMarine(tec, ctx);
                return spaceMarine.map(sm->{
                    sm.setId(SpaceMarine.newId());
                    sm.setCreationDate(new Date());
                    return new AddRequest(sm);
                });
            }
        });

        cr.register("update", new ObjectCommand() {
            @Override
            public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
                if(data.length < 2){
                    tec.print("commandline.request_construct.error.not_enough_arguments");
                    return Optional.empty();
                }
                try {
                    var id = Integer.parseInt(data[1]);
                    if(!SpaceMarine.isIdUsed(id)){
                        tec.print("commandline.request_construct.error.no_such_id");
                        return Optional.empty();
                    }
                    var spaceMarine = constructSpaceMarine(tec, ctx);
                    return spaceMarine.map(sm->{
                        sm.setId(id);
                        sm.setCreationDate(new Date());
                        return new UpdateRequest(sm);
                    });
                }catch(NumberFormatException e){
                    tec.print("commandline.request_construct.error.id_invalid");
                    return Optional.empty();
                }
            }
        });
        cr.register("remove_greater", new ObjectCommand() {
            @Override
            public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
                var sm = constructSpaceMarine(tec, ctx);
                return sm.map(RemoveGreaterRequest::new);
            }
        });
        return cr;
    }
}

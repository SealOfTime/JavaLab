package ru.sealoftime.labjava.client;

import ru.sealoftime.labjava.client.model.ClientRequestExecutor;
import ru.sealoftime.labjava.client.view.ClientLocalizationService;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.EventBus;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.data.concrete.UserData;
import ru.sealoftime.labjava.core.model.data.concrete.Weapon;
import ru.sealoftime.labjava.core.model.events.*;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.requests.network.NetworkRequest;
import ru.sealoftime.labjava.core.model.requests.noargs.*;
import ru.sealoftime.labjava.core.model.requests.object.AddRequest;
import ru.sealoftime.labjava.core.model.requests.object.RemoveGreaterRequest;
import ru.sealoftime.labjava.core.model.requests.object.UpdateRequest;
import ru.sealoftime.labjava.core.model.requests.primitives.FilterLessThanWeaponTypeRequest;
import ru.sealoftime.labjava.core.model.requests.primitives.RemoveByIdRequest;
import ru.sealoftime.labjava.core.model.response.*;
import ru.sealoftime.labjava.core.view.cli.*;
import ru.sealoftime.labjava.core.view.cli.commands.ArglessCommand;
import ru.sealoftime.labjava.core.view.cli.commands.ExecuteScriptCommand;
import ru.sealoftime.labjava.core.view.cli.commands.ObjectCommand;
import ru.sealoftime.labjava.core.view.cli.commands.PrintFieldAscedingChapterCommand;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class ClientApplication {
    public static ConnectionManager connection;

    public static UserData session;
    public static void main(String[] args) {
        var context = new ClientApplicationContext();

        var locale = Locale.forLanguageTag("ru");
        var localization =new ClientLocalizationService(locale);
        context.setLocalization(localization);

        var eventBus = new EventBus();
        context.setEventBus(eventBus);

        var requestExecutor = new ClientRequestExecutor(context);
        context.setRequestExecutor(requestExecutor);

        var cli = initWelcomeDialogue(context);

        var addr  = cli.prompt( "client.prompt.address").orElse("localhost");
        var port = cli.promptValue ("client.prompt.port", Integer::valueOf).orElse (42069);
        connection = new ConnectionManager(addr, port, context);
        var errors = connection.connect();

        if(errors.isPresent()){
            cli.print(errors.get());//todo: logging
        }else{
            requestExecutor.execute(new ShowRequest());

            context.setIsRunning(true);

            var cliThread = new Thread(()->{
                while(context.getIsRunning()) context.getUi().acceptUserInput();
            }, "UserInterface");

            cliThread.start();

            while(context.getIsRunning()){
                connection.receive();
             //   cli.acceptUserInput();
            }
        }

        connection.close();
    }

    private static Optional<UserData> getUserData(CommandLineUserInterface cli){
        var username = cli.prompt("client.prompt.username");
        if(username.isEmpty()) return Optional.empty();
        var password = cli.prompt("client.prompt.password");
        if(password.isEmpty()) return Optional.empty();
        return Optional.of(new UserData(username.get(), password.get()));
    }

    private static CommandLineUserInterface initWelcomeDialogue(ClientApplicationContext ctx){
        var cmds = new CommandRegistry();
        cmds.register("login", new Command() {
            @Override
            public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
                if(data.length < 3)
                    return Optional.empty();
                return Optional.of(new NetworkRequest.LoginRequest(new UserData(data[1], data[2])));
            }
        });
        cmds.register("register", new Command() {
            @Override
            public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
                if(data.length < 3)
                    return Optional.empty();
                return Optional.of(new NetworkRequest.RegisterRequest(new UserData(data[1], data[2])));
            }
        });
        var cli = new CommandLineUserInterface(cmds, ctx);

        ctx.setUi(cli);
        ctx.getEventBus().subscribe(LoginResponse.class, (e)->{
            cli.print("commandline.welcome");
            registerEvents(ctx,cli);
            session = ((LoginResponse)e).data;
            var cr  = registerCommands();
            cli.setCommandRegistry(cr);
        });
        ctx.getEventBus().subscribe(Response.ErrorResponse.class, (err)->{
            var resp = (Response.ErrorResponse)err;
            if(resp.getCommand().equals("login"))
                cli.print(resp.getErrorMessage());
        });

        cli.print("client.login");
        return cli;
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
        bus.subscribe(PrintEvent.class, handler::onPrint);

        var responseHandler = new CommandLineResponseHandler(cli);
        bus.subscribe(Response.class, responseHandler::onResponse);
        bus.subscribe(ListResponse.class, responseHandler::onResponse);
        bus.subscribe(SumOfHealthResponse.class, responseHandler::onResponse);
        bus.subscribe(InfoResponse.class, responseHandler::onResponse);
        bus.subscribe(Response.ErrorResponse.class, responseHandler::onResponse);
    }

    @SuppressWarnings("DuplicatedCode")
    private static CommandRegistry registerCommands()   {
        CommandRegistry cr = new CommandRegistry();

        cr.register("help", new ArglessCommand(HelpRequest::new));
        cr.register("clear", new ArglessCommand(ClearRequest::new));
        cr.register("exit", new ArglessCommand(ExitRequest::new));
        cr.register("history", new ArglessCommand(HistoryRequest::new));
        cr.register("info", new ArglessCommand(InfoRequest::new));
        cr.register("remove_first", new ArglessCommand(RemoveFirstRequest::new));
        //cr.register("save", new ArglessCommand(SaveRequest::new));
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
                    tec.print("commandline.request_construct.error.weaponType_invalid");
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
//                    if(!SpaceMarine.isIdUsed(id)){
//                        tec.print("commandline.request_construct.error.no_such_id");
//                        return Optional.empty();
//                    }
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

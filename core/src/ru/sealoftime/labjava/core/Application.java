package ru.sealoftime.labjava.core;

import ru.sealoftime.labjava.core.model.EventBus;
import ru.sealoftime.labjava.core.model.RequestExecutor;
import ru.sealoftime.labjava.core.model.data.CollectionProvider;
import ru.sealoftime.labjava.core.model.data.DataProvider;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.data.concrete.Weapon;
import ru.sealoftime.labjava.core.model.events.*;
import ru.sealoftime.labjava.core.model.io.CSVFileLoader;
import ru.sealoftime.labjava.core.model.io.FileLoader;
import ru.sealoftime.labjava.core.model.io.SpaceMarineCSVFileLoader;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.requests.noargs.*;
import ru.sealoftime.labjava.core.model.requests.object.AddRequest;
import ru.sealoftime.labjava.core.model.requests.object.RemoveGreaterRequest;
import ru.sealoftime.labjava.core.model.requests.object.UpdateRequest;
import ru.sealoftime.labjava.core.model.requests.primitives.FilterLessThanWeaponTypeRequest;
import ru.sealoftime.labjava.core.model.requests.primitives.RemoveByIdRequest;
import ru.sealoftime.labjava.core.util.FormattedString;
import ru.sealoftime.labjava.core.view.UserInterface;
import ru.sealoftime.labjava.core.view.cli.*;
import ru.sealoftime.labjava.core.view.cli.commands.ArglessCommand;
import ru.sealoftime.labjava.core.view.cli.commands.ExecuteScriptCommand;
import ru.sealoftime.labjava.core.view.cli.commands.ObjectCommand;
import ru.sealoftime.labjava.core.view.cli.commands.PrintFieldAscedingChapterCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Application {

    private ApplicationContext context;
    
    public static void main(String... args){
        var context = new ApplicationContext();

        var cr  = registerCommands();
        var cli = new CommandLineUserInterface(cr, context);

        var eventBus = new EventBus();
        context.setEventBus(eventBus);

        var requestExecutor = new RequestExecutor(context);
        context.setRequestExecutor(requestExecutor);

        var dataProvider = loadData(args[0], cli);
        context.setDataProvider(dataProvider);

        var locale = Locale.forLanguageTag("ru-RU");
        context.setCurrentLanguage(locale);

        var resources = ResourceBundle.getBundle("commandline", locale);
        context.setCurrentLanguageBundle(resources);

        registerEvents(context,cli);
        context.setIsRunning(true);

        cli.print("commandline.welcome");
        while(context.getIsRunning())
            cli.acceptUserInput();
    }

    private static DataProvider loadData(String fileName, CommandLineUserInterface cli){
        if(fileName == null || fileName.isEmpty()){
            fileName = "./data.csv";
            cli.print("application.error.no_file_provided");
        }

        var data = new PriorityQueue<SpaceMarine>();
        var date = new Date();
        var fileLoader = new SpaceMarineCSVFileLoader(fileName);
        try {
            var errors = fileLoader.load(data);
            date = new Date(Files.readAttributes(fileLoader.path(), BasicFileAttributes.class).creationTime().toMillis());
            errors.forEach(err->cli.print(err.getLine(), err.getData()));
        }catch(IOException e){
            if(e instanceof NoSuchFileException)
                cli.print("application.error.no_such_file");
            else e.printStackTrace();
        }

        return new CollectionProvider.PriorityQueueProvider(date, data);
    }

    private static void registerEvents(ApplicationContext ctx, CommandLineUserInterface cli){
        var handler = new CommandLineEventHandler(cli, ctx);
        var bus = ctx.getEventBus();
        bus.subscribe(AddObjectEvent.class, handler::onAdd);
        bus.subscribe(ClearEvent.class, handler::onClear);
        bus.subscribe(HelpEvent.class, handler::onHelp);
        bus.subscribe(InfoEvent.class, handler::onInfo);
        bus.subscribe(RemoveObjectsEvent.class, handler::onRemove);
        bus.subscribe(ShowEvent.class, handler::onShow);
        bus.subscribe(UpdateObjectEvent.class, handler::onUpdate);
        bus.subscribe(ExitEvent.class, handler::onExit);
        bus.subscribe(SumOfHealthEvent.class, handler::onSumOfHealth);
        bus.subscribe(HistoryEvent.class, handler::onHistory);
        bus.subscribe(PrintEvent.class, handler::onPrint);
    }

    private static CommandRegistry registerCommands(){
        CommandRegistry cr = new CommandRegistry();
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
                var spaceMarine = constructSpaceMarine(tec, ctx);
                return spaceMarine.map(sm->{
                    try {
                        var id = Integer.parseInt(data[1]);
                        if(!SpaceMarine.isIdUsed(id)){
                            tec.print("commandline.request_construct.error.no_such_id");
                            return null;
                        }
                        sm.setId(id);
                        sm.setCreationDate(new Date());
                        return new UpdateRequest(sm);
                    }catch(NumberFormatException e){
                        tec.print("commandline.request_construct.error.id_invalid");
                        return null;
                    }
                });
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

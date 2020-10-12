package ru.sealoftime.labjava.core.view.cli;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.*;

public class CommandLineEventHandler {
    private CommandLineUserInterface cli;
    private ApplicationContext ctx;
    public CommandLineEventHandler(CommandLineUserInterface cli, ApplicationContext ctx){
        this.cli = cli;
        this.ctx = ctx;
    }

    public void onClear(Event e){
        this.cli.print("commandline.response.clear");
    }

    public void onExit(Event e){
        this.cli.print("commandline.response.exit");
    }

    public void onHelp(Event e){
        var event = (HelpEvent) e;
        this.cli.print("commandline.response.help");
        for(String line : event.getCommandsInfo()){
            cli.print(line);
        }
    }

    public void onInfo(Event e){
        var event = (InfoEvent) e;
        this.cli.print("commandline.response.info", event.getType(), event.getCreationDate(), event.getSize());
    }

    public void onRemove(Event e){ cli.print("commandline.response.remove");}

    public void onAdd(Event e){ cli.print("commandline.response.add"); }

    public void onUpdate(Event e){ cli.print("commandline.response.update"); }

    public void onShow(Event e){
        var event = (ShowEvent) e;
        for(SpaceMarine sm : event.getRecords()){
            cli.print("commandline.spacemarine", sm.getId(), sm.getName(), sm.getCoordinates(), sm.getCreationDate(), sm.getHealth(), sm.getCategory(), sm.getWeaponType(), sm.getMeleeWeapon(), sm.getChapter());
        }
    }

    public void onSumOfHealth(Event e){
        var event = (SumOfHealthEvent) e;
        cli.print("commandline.response.sumofhealth", event.getSum());
    }


    public void onHistory(Event event) {
        this.cli.printHistory();
    }

    public void onPrint(Event e){
        var event = (PrintEvent) e;
        event.getData().stream().map(Object::toString).forEach(cli::print);
    }
}

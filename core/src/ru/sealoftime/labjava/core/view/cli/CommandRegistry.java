package ru.sealoftime.labjava.core.view.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandRegistry {
    private Map<String, Command> commands;

    public CommandRegistry(){
        this.commands = new HashMap<>();
    }

    public void register(String name, Command command){
        this.commands.put(name, command);
    }

    public Command get(String command){
        return this.commands.get(command);
    }
}

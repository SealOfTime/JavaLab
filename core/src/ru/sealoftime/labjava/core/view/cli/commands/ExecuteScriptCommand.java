package ru.sealoftime.labjava.core.view.cli.commands;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.view.cli.Command;
import ru.sealoftime.labjava.core.view.cli.TextExecutionContext;

import java.util.Optional;
import java.util.function.Consumer;

public class ExecuteScriptCommand extends Command {
    public ExecuteScriptCommand() {
        super();
    }

    @Override
    public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
        var scriptName = data[1];
        //todo: construct execute_script
        return Optional.empty();
    }
}

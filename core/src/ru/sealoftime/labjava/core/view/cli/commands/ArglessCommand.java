package ru.sealoftime.labjava.core.view.cli.commands;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.view.cli.Command;
import ru.sealoftime.labjava.core.view.cli.TextExecutionContext;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArglessCommand extends Command {
    private Supplier<? extends Request> req;
    public ArglessCommand(Supplier<? extends Request> req){
        super();
        this.req = req;
    }

    @Override
    public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
        return Optional.of(req.get());
    }
}
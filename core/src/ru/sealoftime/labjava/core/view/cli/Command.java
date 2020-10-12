package ru.sealoftime.labjava.core.view.cli;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;

import java.util.Optional;

public abstract class Command {
    public abstract Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data);
}

package ru.sealoftime.labjava.core.view.cli;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class Command {

    public abstract Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data);
}

package ru.sealoftime.labjava.core.model.requests;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.response.Response;

import java.io.Serializable;


public abstract class Request implements Serializable {
    private static final long serialVersionUID = -3653177601505192424L;

    public abstract Response execute(ApplicationContext ctx);
}
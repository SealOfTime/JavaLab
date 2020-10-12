package ru.sealoftime.labjava.core.model;

import ru.sealoftime.labjava.core.Application;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;

public class RequestExecutor {

    private ApplicationContext ctx;
    public RequestExecutor(ApplicationContext ctx){
        this.ctx = ctx;
    }

    public Response execute(Request req){
        return req.execute(this.ctx);    }

}

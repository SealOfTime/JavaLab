package ru.sealoftime.labjava.client.model;

import ru.sealoftime.labjava.client.ClientApplication;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.RequestExecutor;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.requests.noargs.ExitRequest;
import ru.sealoftime.labjava.core.model.requests.noargs.HistoryRequest;
import ru.sealoftime.labjava.core.model.response.Response;

import java.util.LinkedList;
import java.util.List;

public class ClientRequestExecutor extends RequestExecutor {
    public ClientRequestExecutor(ApplicationContext ctx) {
        super(ctx);
    }

    private static final LinkedList<Class<? extends Request>> CLIENT_SIDE=new LinkedList<>(
            List.of(
                    ExitRequest.class,
                    HistoryRequest.class,
                    ExitRequest.class
            )
    );

    @Override
    public Response execute(Request req) {
        if(CLIENT_SIDE.contains(req.getClass()))
            return req.execute(this.ctx());

        //todo: send to connection manager
        if(ClientApplication.session != null)
            req.setUserData(ClientApplication.session);
        
        ClientApplication.connection.send(req);
        return null;
      //  return super.execute(req);
    }
}

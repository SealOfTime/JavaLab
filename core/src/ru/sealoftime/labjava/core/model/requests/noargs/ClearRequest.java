package ru.sealoftime.labjava.core.model.requests.noargs;


import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.ClearEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;


@Value
@EqualsAndHashCode(callSuper = true)
public class ClearRequest extends Request {

    @Override
    public Response execute(ApplicationContext ctx) {
        ctx.getDataProvider().clear();
        ctx.getEventBus().notify(new ClearEvent());
        return Response.success("clear");
    }
}

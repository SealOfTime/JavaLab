package ru.sealoftime.labjava.core.model.requests.primitives;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.NoSuchIdEvent;
import ru.sealoftime.labjava.core.model.events.RemoveObjectsEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;

@Value
@EqualsAndHashCode(callSuper = true)
public class RemoveByIdRequest extends Request {
    Integer id;

    @Override
    public Response execute(ApplicationContext ctx) {
        if(ctx.getDataProvider().removeIf((s)->s.getId().equals(this.id))) {
            ctx.getEventBus().notify(new RemoveObjectsEvent(new Integer[]{this.id}));
            return new Response(Response.ResponseStatus.SUCCESS);
        }
        else
            return Response.fail("application.error.no_such_object");
    }
}

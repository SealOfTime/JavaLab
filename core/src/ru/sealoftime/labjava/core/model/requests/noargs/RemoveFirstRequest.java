package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.RemoveObjectsEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;

@Value
@EqualsAndHashCode(callSuper = true)
public class RemoveFirstRequest extends Request {

    @Override
    public Response execute(ApplicationContext ctx) {
        var id = ctx.getDataProvider().removeFirst();
        if(id >= 0) {
            ctx.getEventBus().notify(
                    new RemoveObjectsEvent(
                            new Integer[]{
                                    id
                            }
                    )
            );
            return Response.success("remove_first");
        }
        return Response.fail("remove_first","application.error.remove_first.empty");
    }
}

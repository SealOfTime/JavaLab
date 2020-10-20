package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.ExitEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;

@Value
@EqualsAndHashCode(callSuper = true)
public class ExitRequest extends Request {

    @Override
    public Response execute(ApplicationContext ctx) {
        ctx.setIsRunning(false);
        ctx.getEventBus().notify(new ExitEvent());
        return Response.success("exit");
    }
}

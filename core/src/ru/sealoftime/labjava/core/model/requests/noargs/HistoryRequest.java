package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.HistoryEvent;
import ru.sealoftime.labjava.core.model.requests.Request;

@Value
@EqualsAndHashCode(callSuper = true)
public class HistoryRequest extends Request {
    @Override
    public Response execute(ApplicationContext ctx) {
        ctx.getEventBus().notify(new HistoryEvent());
    }
}

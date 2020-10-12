package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.ShowEvent;
import ru.sealoftime.labjava.core.model.requests.Request;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class ShowRequest extends Request {
    @Override
    public Response execute(ApplicationContext ctx) {
        ctx.getEventBus().notify(
                new ShowEvent(
                        List.of(
                                ctx.getDataProvider().toArray(SpaceMarine[]::new)
                        )
                )
        );
    }
}

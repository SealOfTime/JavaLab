package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.ShowEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.ListResponse;
import ru.sealoftime.labjava.core.model.response.Response;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class ShowRequest extends Request {
    @Override
    public Response execute(ApplicationContext ctx) {
        return new ListResponse<SpaceMarine>("show",
                List.of( ctx.getDataProvider().toArray(SpaceMarine[]::new) ));
    }
}

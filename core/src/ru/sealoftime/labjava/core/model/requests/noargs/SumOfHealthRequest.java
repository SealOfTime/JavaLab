package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.SumOfHealthEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.model.response.SumOfHealthResponse;

@Value
@EqualsAndHashCode(callSuper = true)
public class SumOfHealthRequest extends Request {

    @Override
    public Response execute(ApplicationContext ctx) {
        var sum = ctx.getDataProvider().stream()
                                       .map( SpaceMarine::getHealth )
                                       .reduce(0, Integer::sum);
        return new SumOfHealthResponse(sum);
    }
}

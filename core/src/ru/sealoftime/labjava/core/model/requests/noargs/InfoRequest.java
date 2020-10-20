package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.InfoEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.InfoResponse;
import ru.sealoftime.labjava.core.model.response.Response;

@Value
@EqualsAndHashCode(callSuper = true)
public class InfoRequest extends Request {
    @Override
    public Response execute(ApplicationContext ctx) {
        var dp = ctx.getDataProvider();

        return new InfoResponse(
            dp.getType(),
            dp.getCreationDate(),
            dp.size()
        );
        //
    }
}

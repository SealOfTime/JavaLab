package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;

@Value
@EqualsAndHashCode(callSuper = true)
public class SaveRequest extends Request {

    @Override
    public Response execute(ApplicationContext ctx) {
        //todo: save request
    }
}

package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;

import java.io.IOException;

@Value
@EqualsAndHashCode(callSuper = true)
public class SaveRequest extends Request {

    @Override
    public Response execute(ApplicationContext ctx) {
        //todo: save request
        try {
            ctx.getDataUnloader().save(ctx.getDataProvider());
            return Response.success("save");
        }catch(IOException e){
            return Response.fail("save", "application.error.save.unknown"); //todo: add details to responsefail
        }
    }
}

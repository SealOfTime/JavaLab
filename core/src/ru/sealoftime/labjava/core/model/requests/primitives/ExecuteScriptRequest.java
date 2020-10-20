package ru.sealoftime.labjava.core.model.requests.primitives;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;


@Data
@EqualsAndHashCode(callSuper = true)
public class ExecuteScriptRequest extends Request {
    String fileName;

    @Override
    public Response execute(ApplicationContext ctx) {
        //TODO: execute script блять
        return Response.fail("execute_script", "application.error.not_implemented");
    }
}

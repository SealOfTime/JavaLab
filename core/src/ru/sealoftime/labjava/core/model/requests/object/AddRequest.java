package ru.sealoftime.labjava.core.model.requests.object;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.AddObjectEvent;
import ru.sealoftime.labjava.core.model.response.Response;

import java.util.Date;

public class AddRequest extends ObjectRequest{

    public AddRequest(SpaceMarine object) {
        super(object);
    }

    @Override
    public Response execute(ApplicationContext ctx) {
        var object = this.getObject();

        object.setId(SpaceMarine.newId());
        object.setCreationDate(new Date());

        ctx.getDataProvider().add(object);
        ctx.getEventBus().notify(new AddObjectEvent(object));
        return Response.success("add");
    }
}

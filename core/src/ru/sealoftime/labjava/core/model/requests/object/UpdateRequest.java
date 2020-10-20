package ru.sealoftime.labjava.core.model.requests.object;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.UpdateObjectEvent;
import ru.sealoftime.labjava.core.model.response.Response;

public class UpdateRequest extends ObjectRequest {
    public UpdateRequest(SpaceMarine object) {
        super(object);
    }

    @Override
    public Response execute(ApplicationContext ctx) {
        var sm = this.getObject();
        if(ctx.getDataProvider().removeIf(other->other.getId().equals(sm.getId()))) {
            ctx.getDataProvider().add(sm);
            ctx.getEventBus().notify(new UpdateObjectEvent(sm));
            return Response.success("update");
        }
        return Response.fail("update", "application.error.update_request.no_such_id");
    }
}

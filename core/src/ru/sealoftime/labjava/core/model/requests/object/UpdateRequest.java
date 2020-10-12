package ru.sealoftime.labjava.core.model.requests.object;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.UpdateObjectEvent;

public class UpdateRequest extends ObjectRequest {
    public UpdateRequest(SpaceMarine object) {
        super(object);
    }

    @Override
    public Response execute(ApplicationContext ctx) {
        var sm = this.getObject();
        ctx.getDataProvider().removeIf(
                other->other.getId().equals(sm.getId())
        );
        ctx.getDataProvider().add(sm);
        ctx.getEventBus().notify(new UpdateObjectEvent(sm));
    }
}

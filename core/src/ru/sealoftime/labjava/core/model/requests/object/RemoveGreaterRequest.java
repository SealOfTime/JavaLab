package ru.sealoftime.labjava.core.model.requests.object;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.RemoveObjectsEvent;
import ru.sealoftime.labjava.core.model.response.Response;

import java.util.stream.Collectors;

public class RemoveGreaterRequest extends ObjectRequest{
    public RemoveGreaterRequest(SpaceMarine object) {
        super(object);
    }

    @Override
    public Response execute(ApplicationContext ctx) {
        var relative = this.getObject();
        var toRemove = ctx.getDataProvider()
                          .stream()
                          .filter ( s->s.compareTo(relative)>0 )
                          .collect( Collectors.toList()        );
        ctx.getDataProvider().removeAll(toRemove);
        ctx.getEventBus().notify(
                new RemoveObjectsEvent(
                        toRemove.stream()
                                .map    ( SpaceMarine::getId  )
                                .collect( Collectors.toList() )
                                .toArray( Integer[]::new      )
                )
        );
        if(!toRemove.isEmpty())
            return Response.success();
        else
            return Response.fail("application.error.remove_greater.no_objects_greater");
    }
}

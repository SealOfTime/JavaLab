package ru.sealoftime.labjava.server;

import ru.sealoftime.labjava.core.model.EventBus;
import ru.sealoftime.labjava.core.model.events.Event;

public class RemoteEventBus extends EventBus {
    @Override
    public void notify(Event e) {
        ServerApplication.clients.forEach( client ->
                client.send(e)
        );
        super.notify(e);
    }
}

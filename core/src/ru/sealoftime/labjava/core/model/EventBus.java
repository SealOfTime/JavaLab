package ru.sealoftime.labjava.core.model;

import ru.sealoftime.labjava.core.model.events.Event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    private HashMap<Class<? extends Event>, List<Consumer<Event>>> subscribtions;

    public EventBus(){
        this.subscribtions = new HashMap<>();
    }
    // It's guaranteed that observer will only be called on such an event that it does expect to accept.
    public void subscribe(Class<? extends Event> e, Consumer<Event> observer){
        List<Consumer<Event>> subscription = this.subscribtions.getOrDefault(e, new LinkedList<>());
        subscription.add(observer);
        this.subscribtions.put(e, subscription);
    }

    public void notify(Event e){
        var handlers = this.subscribtions.get(e.getClass());
        if(handlers == null) {
            System.out.println("No handlers for " + e.getClass().getSimpleName());
            return;
        }
        handlers.forEach(
                c -> c.accept(e)
        );
    }

}

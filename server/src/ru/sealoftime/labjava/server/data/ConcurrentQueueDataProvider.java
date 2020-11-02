package ru.sealoftime.labjava.server.data;

import ru.sealoftime.labjava.core.model.data.CollectionProvider;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.PriorityQueue;

public class ConcurrentQueueDataProvider extends CollectionProvider {
    private Collection<SpaceMarine> ref;
    private PriorityQueue<SpaceMarine> qRef;
    public SpaceMarine getFirst(){
        return this.qRef.peek();
    }

    public ConcurrentQueueDataProvider(Date creationDate, PriorityQueue<SpaceMarine> collection) {
        super("Synchronized Collection from PriorityQueue", creationDate, Collections.synchronizedCollection(collection));
        ref=this.getCollection();
        qRef = collection;
    }

    @Override
    public int removeFirst() {
        var sm = qRef.peek();
        if(ref.remove(sm) && sm != null) {
            return sm.getId();
        }
        else return -1;
    }
}

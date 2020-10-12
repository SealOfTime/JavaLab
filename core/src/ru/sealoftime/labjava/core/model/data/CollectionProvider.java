package ru.sealoftime.labjava.core.model.data;

import lombok.experimental.Delegate;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;

import javax.security.auth.login.CredentialException;
import java.util.Collection;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Spliterator;

public abstract class CollectionProvider implements DataProvider{
    @Delegate
    private Collection<SpaceMarine> collection;

    private final Date creationDate;

    private final String type;
    public CollectionProvider(String type, Date creationDate, Collection<SpaceMarine> collection){
        this.creationDate = creationDate;
        this.collection = collection;
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    public static class PriorityQueueProvider extends CollectionProvider{
        private final PriorityQueue<SpaceMarine> qRef;

        public PriorityQueueProvider(Date creationDate, PriorityQueue<SpaceMarine> collection){
            super("PriorityQueue", creationDate, collection);
            this.qRef = collection;
        }

        @Override
        public int removeFirst(){
            return this.qRef.poll().getId();
        }

    }
}

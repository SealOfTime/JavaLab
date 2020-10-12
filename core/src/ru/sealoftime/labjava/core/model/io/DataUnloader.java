package ru.sealoftime.labjava.core.model.io;

import java.io.IOException;
import java.util.Collection;

public abstract class DataUnloader<T> {
    /* Saves the collection. */
    public abstract void save(Collection<T> from) throws IOException;
}
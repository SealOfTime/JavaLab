package ru.sealoftime.labjava.core.model.io;

import ru.sealoftime.labjava.core.util.FormattedString;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class DataLoader<T> {
    /* Modifies Collection @returns errors */
    public abstract <C extends Collection<T>> List<FormattedString> load(C dest) throws IOException;
}
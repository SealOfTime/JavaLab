package ru.sealoftime.labjava.client;

import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.io.DataLoader;
import ru.sealoftime.labjava.core.util.FormattedString;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class RemoteSpaceMarineDataLoader extends DataLoader<SpaceMarine> {
    //TODO: load from server

    @Override
    public <C extends Collection<SpaceMarine>> List<FormattedString> load(C dest) throws IOException {
        return null;
    }
}

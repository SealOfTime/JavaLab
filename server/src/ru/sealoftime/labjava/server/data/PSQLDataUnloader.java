package ru.sealoftime.labjava.server.data;

import jdk.jshell.spi.ExecutionControl;
import lombok.SneakyThrows;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.io.DataUnloader;

import java.io.IOException;
import java.util.Collection;

public class PSQLDataUnloader extends DataUnloader<SpaceMarine> {

    @Override
    @SneakyThrows
    public void save(Collection<SpaceMarine> from) throws IOException {
        throw new ExecutionControl.NotImplementedException("Not yet done saving");
        //todo: save
    }
}

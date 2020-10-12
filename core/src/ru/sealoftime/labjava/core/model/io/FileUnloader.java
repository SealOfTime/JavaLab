package ru.sealoftime.labjava.core.model.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class FileUnloader<T> extends DataUnloader<T> {
    private Path file;

    private Function<T, Object[]> deconstructor;
    public Function<T, Object[]> deconstructor(){ return this.deconstructor; }

    public FileUnloader(String fileName, Function<T, Object[]> deconstructor){
        this.file = FileSystems.getDefault().getPath(fileName);
        this.deconstructor = deconstructor;
    }

    @Override
    public void save(Collection<T> from) throws IOException{
        try(BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file)); ){   
            Writer wr = new OutputStreamWriter(bos);
            wr.write(compose(from.stream()));
            wr.flush();
        }
    }

    /* Composes the stream of objects into a single line using deconstructor to deconstract separate entries. */
    public abstract String compose(Stream<T> entries) throws IOException;
}
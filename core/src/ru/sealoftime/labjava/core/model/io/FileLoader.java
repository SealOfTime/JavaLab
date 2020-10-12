package ru.sealoftime.labjava.core.model.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import ru.sealoftime.labjava.core.util.Either;
import ru.sealoftime.labjava.core.util.FormattedString;

public abstract class FileLoader<T> extends DataLoader<T> {
    private Path file;
    public Path path(){ return this.file; }
    public void path(final String path){ this.file=FileSystems.getDefault().getPath(path);}

    private final Function<String[], T> reconstructor;
    public Function<String[], T> reconstructor(){ return this.reconstructor; }
    public FileLoader(final String path, final Function<String[], T> reconstructor){
        path(path);
        this.reconstructor = reconstructor;
    }

    @Override
    public <R extends Collection<T>> List<FormattedString> load(final R populatee) throws IOException{//TODO: return Pair<Collection, List>
        final List<FormattedString> errors = new LinkedList<>();
        this.parse(
            new InputStreamReader(
                new BufferedInputStream(
                    new FileInputStream(
                        this.path().toString()
                    )
                )
            ))
            .forEach((e)->{
                if(e.isLeft()) populatee.add(e.left());
                else if(e.isRight()) errors.add(e.right());
            });
        return errors;
    }
    /* Converts file into a stream of objects, using reconstructor as its constructors. */
    public abstract Stream<Either<T, FormattedString>> parse(Reader data) throws IOException;
}
package ru.sealoftime.labjava.core.model.io;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ru.sealoftime.labjava.core.util.Either;
import ru.sealoftime.labjava.core.util.FormattedString;


public class CSVFileLoader<T> extends FileLoader<T> {

    public CSVFileLoader(String path, Function<String[], T> reconstructor) {
        super(path, reconstructor);        
    }

    @Override
    public Stream<Either<T, FormattedString>> parse(Reader reader) throws IOException {
        CSVParser csvRecords = CSVFormat.EXCEL.withDelimiter(';').parse(reader);
        return StreamSupport.<CSVRecord>stream(csvRecords.spliterator(), false)
                        .map((rec)->{
                            try{
                                List<String> whatTheFuck = new ArrayList<String>();
                                rec.forEach(whatTheFuck::add);
                                return Either.left(
                                    this.reconstructor().apply(
                                        whatTheFuck.toArray(String[]::new)
                                        )
                                );
                            } catch(IllegalArgumentException e){
                                return Either.right(
                                    new FormattedString("application.error.fileloading.csv", rec.getRecordNumber(), e.getMessage())
                                );
                            }
                        });
    }

}
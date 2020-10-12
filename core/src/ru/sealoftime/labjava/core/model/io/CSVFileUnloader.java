package ru.sealoftime.labjava.core.model.io;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CSVFileUnloader<T> extends FileUnloader<T> {

    private boolean currentOperationSuccess = true;

    public CSVFileUnloader(String fileName, Function<T, Object[]> deconstructor) {
        super(fileName, deconstructor);
    }

    //Prints record catching the IOExceptions to interrupt process.
    private void safePrintRecord(CSVPrinter printer, Object[] obj){
        try{
            printer.printRecord(obj);
        }catch(IOException e){
            if(currentOperationSuccess) currentOperationSuccess = false;
        }
    }

    @Override
    public String compose(Stream<T> entries) throws IOException {
        StringBuilder result = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(result, CSVFormat.EXCEL.withDelimiter(';'));
        
        this.currentOperationSuccess = true;
        
        entries.map(this.deconstructor())
                .forEach((e)->{this.safePrintRecord(printer, e);});
                
        if(!this.currentOperationSuccess)
            throw new IOException("An error occured during deconstruction of objects. Representitive decomposition must be incomplete/faulted");

        return result.toString();
    }

}
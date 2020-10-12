package ru.sealoftime.labjava.core.view.cli.commands;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.requests.primitives.PrintFieldAscendingChapterRequest;
import ru.sealoftime.labjava.core.view.cli.Command;
import ru.sealoftime.labjava.core.view.cli.TextExecutionContext;

import java.util.Optional;

public class PrintFieldAscedingChapterCommand extends Command {
    @Override
    public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
        if(data.length > 1) {
            var field = data[1];
            if (PrintFieldAscendingChapterRequest.fieldsToGetters.containsKey(field))
                return Optional.of(new PrintFieldAscendingChapterRequest(field));
            else tec.print("commandline.request_construct.error.nosuchfield");
        }
        else tec.print("commandline.request_construct.error.notenougharguments", "print_field_ascending_chapter");

        return Optional.empty();
    }
}

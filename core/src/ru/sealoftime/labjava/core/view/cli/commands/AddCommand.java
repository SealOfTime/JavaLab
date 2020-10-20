package ru.sealoftime.labjava.core.view.cli.commands;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.requests.object.AddRequest;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.view.cli.TextExecutionContext;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class AddCommand extends ObjectCommand {
    public AddCommand() {
        super();
    }

    @Override
    public Optional<Request> constructRequest(TextExecutionContext tec, ApplicationContext ctx, String[] data) {
        var objectOrNot = this.constructSpaceMarine(tec, ctx);
        return objectOrNot.map(sm -> {
            sm.setId(SpaceMarine.newId());
            sm.setCreationDate(new Date());
            return new AddRequest(sm);
        });
    }

}

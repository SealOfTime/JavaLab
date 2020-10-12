package ru.sealoftime.labjava.core.model.requests.noargs;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.HelpEvent;
import ru.sealoftime.labjava.core.model.requests.Request;

import java.util.Arrays;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class HelpRequest extends Request {
    static List<String> commandsInfo = Arrays.asList(
            "commandline.help.help",
            "commandline.help.clear",
            "commandline.help.exit",
            "commandline.help.history",
            "commandline.help.info",
            "commandline.help.remove_first",
            "commandline.help.save",
            "commandline.help.sum_of_health",
            "commandline.help.show",
            "commandline.help.execute_script",
            "commandline.help.filter_less_than_weapon_type",
            "commandline.help.print_field_ascending_chapter",
            "commandline.help.remove_by_id",
            "commandline.help.add",
            "commandline.help.update",
            "commandline.help.remove_greater"
    );

    @Override
    public Response execute(ApplicationContext ctx) {
        ctx.getEventBus().notify(new HelpEvent(commandsInfo));
    }
}

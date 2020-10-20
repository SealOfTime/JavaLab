package ru.sealoftime.labjava.core.model.requests.primitives;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.PrintEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.ListResponse;
import ru.sealoftime.labjava.core.model.response.Response;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


@Value
@EqualsAndHashCode(callSuper = true)
public class PrintFieldAscendingChapterRequest extends Request {
    String fieldName;
    public static final HashMap<String, Function<SpaceMarine, ? extends Serializable>> fieldsToGetters=new HashMap<>();
    static{
        fieldsToGetters.put( "id",           SpaceMarine::getId          );
        fieldsToGetters.put( "name",         SpaceMarine::getName        );
        fieldsToGetters.put( "creationDate", SpaceMarine::getCreationDate);
        fieldsToGetters.put( "category",     SpaceMarine::getCategory    );
        fieldsToGetters.put( "coordinates",  SpaceMarine::getCoordinates );
        fieldsToGetters.put( "weaponType",   SpaceMarine::getWeaponType  );
        fieldsToGetters.put( "health",       SpaceMarine::getHealth      );
        fieldsToGetters.put( "meleeWeapon",  SpaceMarine::getMeleeWeapon );
        fieldsToGetters.put( "chapter",      SpaceMarine::getChapter     );
    }

    @Override
    public Response execute(ApplicationContext ctx) {
        var list = ctx.getDataProvider().stream ()
                                        .sorted ( Comparator.comparing(SpaceMarine::getChapter) )
                                        .map    ( fieldsToGetters.get(this.fieldName)           )
                                        .collect( Collectors.toList()                           );
        return new ListResponse<>("print_field_ascending_chapter", list);
    }
}

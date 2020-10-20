package ru.sealoftime.labjava.core.model.requests.primitives;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.data.concrete.Weapon;
import ru.sealoftime.labjava.core.model.events.ShowEvent;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.ListResponse;
import ru.sealoftime.labjava.core.model.response.Response;

import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FilterLessThanWeaponTypeRequest extends Request {
    Weapon weapon;

    @Override
    public Response execute(ApplicationContext ctx) {
        var filtered = ctx.getDataProvider()
                          .stream()
                          .filter ( sm -> sm.getWeaponType().compareTo(this.weapon) > 0)
                          .collect( Collectors.toList() );
        return new ListResponse<>("filter_less_than_weapon_type", filtered);
    }
}

package ru.sealoftime.labjava.core.model.requests.object;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.requests.Request;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal = true)
public abstract class ObjectRequest extends Request {
    SpaceMarine object;

}

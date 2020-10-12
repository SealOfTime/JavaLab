package ru.sealoftime.labjava.core.model.events;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddObjectEvent extends Event{
    SpaceMarine object;
}

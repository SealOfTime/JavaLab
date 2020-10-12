package ru.sealoftime.labjava.core.model.events;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;

import java.util.LinkedList;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class ShowEvent extends Event {
    List<SpaceMarine> records;
}

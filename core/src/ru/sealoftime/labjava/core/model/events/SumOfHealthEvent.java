package ru.sealoftime.labjava.core.model.events;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class SumOfHealthEvent extends Event{
    int sum;
}

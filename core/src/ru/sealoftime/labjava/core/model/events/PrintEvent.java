package ru.sealoftime.labjava.core.model.events;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class PrintEvent extends Event{
    List<?> data;
}

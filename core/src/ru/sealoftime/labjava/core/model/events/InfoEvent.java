package ru.sealoftime.labjava.core.model.events;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Date;

@Value
@EqualsAndHashCode(callSuper = true)
public class InfoEvent extends Event {
    String type; //Maybe replace with enum?
    Date creationDate;
    Integer size;
}

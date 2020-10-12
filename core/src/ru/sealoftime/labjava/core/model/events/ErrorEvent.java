package ru.sealoftime.labjava.core.model.events;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ErrorEvent extends Event {
    ErrorType type;

    enum ErrorType{
        NO_SUCH_ID
    }
}

package ru.sealoftime.labjava.core.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.sealoftime.labjava.core.model.events.Event;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Response extends Event {
    ResponseStatus status;
    String command;

    public static Response fail(String cmd, String errorMessage){
        return new ErrorResponse(cmd, errorMessage);
    }

    public static Response success(String cmd){
        return new Response(ResponseStatus.SUCCESS, cmd);
    }

    public enum ResponseStatus{
        SUCCESS, FAIL
    }

    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class ErrorResponse extends Response{
        String errorMessage;

        public ErrorResponse(String cmd, String errorMessage) {
            super(ResponseStatus.FAIL, cmd);
            this.errorMessage = errorMessage;
        }
    }
}

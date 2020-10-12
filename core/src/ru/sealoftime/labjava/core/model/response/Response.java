package ru.sealoftime.labjava.core.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Response implements Serializable {
    ResponseStatus status;

    public static Response fail(String errorMessage){
        return new ErrorResponse(errorMessage);
    }

    public static Response success(){
        return new Response(ResponseStatus.SUCCESS);
    }

    public enum ResponseStatus{
        SUCCESS, FAIL
    }

    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class ErrorResponse extends Response{
        String errorMessage;

        public ErrorResponse(String errorMessage) {
            super(ResponseStatus.FAIL);
            this.errorMessage = errorMessage;
        }
    }
}

package ru.sealoftime.labjava.core.model.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InfoResponse extends Response {
    String type; //Maybe replace with enum?
    Date creationDate;
    Integer size;
    public InfoResponse(String type, Date creationDate, Integer size) {
        super(ResponseStatus.SUCCESS, "info");
        this.type = type;
        this.creationDate = creationDate;
        this.size = size;
    }

}

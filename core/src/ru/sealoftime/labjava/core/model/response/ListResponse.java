package ru.sealoftime.labjava.core.model.response;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@FieldDefaults(makeFinal = true, level= AccessLevel.PRIVATE)
public class ListResponse<T extends Serializable> extends Response{
    List<T> data;
    public ListResponse(String cmd, List<T> data) {
        super(ResponseStatus.SUCCESS, cmd);
        this.data = data;
    }

}

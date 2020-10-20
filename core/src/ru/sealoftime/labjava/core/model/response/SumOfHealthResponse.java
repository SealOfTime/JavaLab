package ru.sealoftime.labjava.core.model.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SumOfHealthResponse extends Response{
    int sum;
    public SumOfHealthResponse(int sum) {
        super( ResponseStatus.SUCCESS, "sum_of_health");
        this.sum=sum;
    }
}

package ru.sealoftime.labjava.core.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sealoftime.labjava.core.model.data.concrete.UserData;

public class LoginResponse extends Response{
    public final UserData data;
    public LoginResponse(UserData data) {
        super(ResponseStatus.SUCCESS, "login");
        this.data = data;
    }
}

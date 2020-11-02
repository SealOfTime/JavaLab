package ru.sealoftime.labjava.core.model.data.concrete;

import lombok.Data;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

@Value
public class UserData implements Serializable {
    String username;
    String password;
}

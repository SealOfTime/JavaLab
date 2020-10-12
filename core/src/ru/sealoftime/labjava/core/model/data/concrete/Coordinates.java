package ru.sealoftime.labjava.core.model.data.concrete;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.sf.oval.constraint.Max;
import net.sf.oval.constraint.Min;

import java.io.Serializable;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Coordinates implements Serializable {
             float x;
    @Max(45) long  y; //Максимальное значение поля: 45
}

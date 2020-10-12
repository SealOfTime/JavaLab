package ru.sealoftime.labjava.core.model.data.concrete;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.Range;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chapter implements Comparable<Chapter>, Serializable {
    @NotEmpty               String  name;         //Поле не может быть null, Строка не может быть пустой
    @Range(min=0, max=1000) long    marinesCount; //Значение поля должно быть больше 0, Максимальное значение поля: 1000

    @Override
    public int compareTo(Chapter chapter) {
        return (int) (this.marinesCount - chapter.marinesCount);
    }
}

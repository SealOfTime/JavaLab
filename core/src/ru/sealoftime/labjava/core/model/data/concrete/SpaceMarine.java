package ru.sealoftime.labjava.core.model.data.concrete;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.sf.oval.Validator;
import net.sf.oval.constraint.*;
import ru.sealoftime.labjava.core.util.Either;
import ru.sealoftime.labjava.core.util.FormattedString;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceMarine implements Serializable, Comparable<SpaceMarine> {
    @ValidateWithMethod(methodName="isIdValid", parameterType=Integer.class)
    @NotNull                                    //Поле не может быть null, Значение поля должно быть больше 0, Значение
                 Integer          id;           // этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotEmpty    String           name;         //Поле не может быть null, Строка не может быть пустой
    @AssertValid
    @NotNull     Coordinates      coordinates;  //Поле не может быть null
    @NotNull     Date             creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @Min(0)      int              health;       //Значение поля должно быть больше 0
                 AstartesCategory category;     //Поле может быть null
    @NotNull     Weapon           weaponType;   //Поле не может быть null
    @NotNull     MeleeWeapon      meleeWeapon;  //Поле не может быть null
    @AssertValid Chapter          chapter;      //Поле может быть null

    private static Validator validator = new Validator();

    public void setId(@AssertFieldConstraints Integer id){
        this.id = id;
    }

    private static BitSet ids = new BitSet();
    public static synchronized void freeId(int id){ ids.clear(id-1); }
    public static synchronized void markIdUsed(int id){ ids.set(id-1); }
    public static boolean           isIdUsed(int id){ return ids.get(id-1); }
    public static synchronized Integer newId(){
        int id = ids.nextClearBit(0);//get the last free id, including those, that were gaped because of removals
        markIdUsed(id+1);
        return id+1; //to skip zero
    }

    public boolean isIdValid(Integer value) {
        return value > 0 && !isIdUsed(value);
    }

    @Override
    public int compareTo(SpaceMarine spaceMarine) {
        return spaceMarine.health - this.health;
    }



}

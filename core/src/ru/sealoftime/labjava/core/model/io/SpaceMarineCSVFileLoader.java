package ru.sealoftime.labjava.core.model.io;

import lombok.Value;
import net.sf.oval.Validator;
import ru.sealoftime.labjava.core.model.data.DynamicBuilder;
import ru.sealoftime.labjava.core.model.data.concrete.*;
import ru.sealoftime.labjava.core.util.Either;
import ru.sealoftime.labjava.core.util.UnsafeFunction;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class SpaceMarineCSVFileLoader extends CSVFileLoader<SpaceMarine> {
    private static Validator validator = new Validator();
    public SpaceMarineCSVFileLoader(String fileName){
        super(fileName, SpaceMarineCSVFileLoader::reconstruct);

    }
    private static Date parseDate(String line) throws ParseException {
        return DateFormat.getDateInstance().parse(line);
    }

    //todo: это не билдер, это ваще кек и рофл
    private static final DynamicBuilder<SpaceMarine> builder = new DynamicBuilder<SpaceMarine>()
            .simple("id",   false, SpaceMarine::setId,   Integer::parseInt)
            .simple("name", false, SpaceMarine::setName, s->s            )
            .complex("coordinates", false, SpaceMarine::setCoordinates, Coordinates::new, new DynamicBuilder<Coordinates>()
                .simple("x", false, Coordinates::setX, Float::parseFloat)
                .simple("y", false, Coordinates::setY, Long::parseLong))
            .simple("creationDate", false, SpaceMarine::setCreationDate, SpaceMarineCSVFileLoader::parseDate)
            .simple("health", false, SpaceMarine::setHealth, Integer::parseInt)
            .simple("category", true, SpaceMarine::setCategory, AstartesCategory::valueOf)
            .simple("weaponType", false, SpaceMarine::setWeaponType, Weapon::valueOf)
            .simple("meleeWeapon", false, SpaceMarine::setMeleeWeapon, MeleeWeapon::valueOf)
            .complex("chapter", true, SpaceMarine::setChapter, Chapter::new, new DynamicBuilder<Chapter>()
                .simple("name", false, Chapter::setName, s->s)
                .simple("marinesCount", false, Chapter::setMarinesCount, Long::parseLong));

    @SuppressWarnings("unchecked")
    public static SpaceMarine reconstruct(String[] lines){
        SpaceMarine sm = new SpaceMarine();
        if(lines.length < 10)
            throw new IllegalArgumentException("commandline.request_construct.error.spacemarine.not_enough_fields");
        var index = 0;
        for(DynamicBuilder<SpaceMarine>.Property<?> p : builder.getProperties()){
          if(!p.isComplex()){
              reconstructSimpleProperty(sm, lines[index], (DynamicBuilder<SpaceMarine>.SimpleProperty<?>)p);
          }else{
              index = reconstructComplexProperty(sm, lines, index, (DynamicBuilder<SpaceMarine>.ComplexProperty<?>)p);
          }

        }

        return sm;
    }
    private static <O, T> void reconstructSimpleProperty(O obj, String line, DynamicBuilder<O>.SimpleProperty<T> p){
        var valueOrNot = valueFromString(line, p.getName(), p.isNullable(), obj, p.getParser());
        if(valueOrNot.isRight())
            throw new IllegalArgumentException(valueOrNot.right());
        p.getSetter().accept(obj, valueOrNot.left());
    }

    @SuppressWarnings("unchecked")
    private static <O, T> int reconstructComplexProperty(O obj, String[] lines, int i, DynamicBuilder<O>.ComplexProperty<T> p){
        var object = p.getDataSupplier().get();
        var index = i;
        for(DynamicBuilder<T>.Property<?> pp : p.getBuilder().getProperties()){
            if(!pp.isComplex()){
                reconstructSimpleProperty(object,
                        lines[index++],
                        (DynamicBuilder<T>.SimpleProperty<?>)pp);
            }
        }
        p.getSetter().accept(obj, object);
        return index;
    }

    private static Either<Coordinates, String> reconstructCoordinates(String[] lines){
//        var xOrNot = valueFromString(lines[2], )
        return null;
    }

    private static <R, T extends Exception> Either<R, String> valueFromString(String raw, String name, boolean nullable, Object obj, UnsafeFunction<String, R, T> parser){
        try{
            R value = null;
            if(!raw.isBlank())
                value = parser.apply(raw);

            var violations = validator.validateFieldValue(obj, obj.getClass().getDeclaredField(name), value);//TODO: this is bs, Validator has its own messaging shit
            if(violations.isEmpty() && (value !=null || nullable))
                return Either.left(value);
            else
                return Either.right("commandline.request_construct.error." + name + "_invalid");
        }
        catch(NoSuchFieldException | SecurityException e){
            e.printStackTrace();
            return Either.right("commandline.request_construct.error.unexpected");
        }
        catch(Exception e){
            return Either.right("commandline.request_construct.error." + name +"_invalid");
        }
    }
}

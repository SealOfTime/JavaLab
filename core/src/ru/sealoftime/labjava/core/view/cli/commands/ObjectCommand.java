package ru.sealoftime.labjava.core.view.cli.commands;

import net.sf.oval.Validator;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.*;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.util.Either;
import ru.sealoftime.labjava.core.util.UnsafeFunction;
import ru.sealoftime.labjava.core.view.cli.Command;
import ru.sealoftime.labjava.core.view.cli.TextExecutionContext;


import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;

public abstract class ObjectCommand extends Command {
    //TODO: bullshit - flyweight with state that is mutated
    Validator validator;
    NumberFormat numberFormat;
    SpaceMarine object;
    TextExecutionContext tec;

    public ObjectCommand() {
        super();
    }

    public Optional<SpaceMarine> constructSpaceMarine(TextExecutionContext tec, ApplicationContext ctx){
        this.numberFormat = NumberFormat.getInstance(ctx.getLocalization().getLocale());
        this.validator = new Validator();
        var object = new SpaceMarine();
        this.tec = tec;
        if(setValueFromInput("name", "spacemarine.name", false, object, object::setName, (s)->s) &&
           setCoordinatesFromInput(numberFormat, object::setCoordinates) &&
           setValueFromInput("health", "spacemarine.health", false, object, object::setHealth, Integer::valueOf) &&
           setValueFromInput("category", "spacemarine.category", true, object,object::setCategory, AstartesCategory::valueOf) &&
           setValueFromInput("weaponType", "spacemarine.weaponType", false, object, object::setWeaponType, Weapon::valueOf) &&
            setValueFromInput("meleeWeapon", "spacemarine.meleeWeapon", false, object, object::setMeleeWeapon, MeleeWeapon::valueOf) &&
           setChapterFromInput(numberFormat, object::setChapter)){
            return Optional.of(object);
        }
        return Optional.empty();
    }

    private boolean setChapterFromInput(NumberFormat numberFormat, Consumer<Chapter> setter){
        var chapter = new Chapter();
        var nameOrNot = this.tec.promptOrCancel("commandline.request_construct.chapter.name",
                (s)->this.valueFromString(s, "name", true, chapter, l->l));
        if(nameOrNot.isLeft()) {
            var name = nameOrNot.left();
            if(name == null) {
                setter.accept(null);
                return true;
            }else{
                chapter.setName(name);
                var marinesCountOrNot = this.tec.promptOrCancel("commandline.request_construct.chapter.marinesCount",
                        (s)->this.valueFromString(s, "marinesCount", true, chapter, Long::valueOf));
                if(marinesCountOrNot.isLeft()){
                    var marinesCount = marinesCountOrNot.left();
                    if(marinesCount == null) {
                        setter.accept(null);
                    }else{
                        chapter.setMarinesCount(marinesCount);
                        setter.accept(chapter);
                    }
                    return true;
                } else{
                    this.tec.print(marinesCountOrNot.right());
                    return false;
                }
            }
        }
        else{
            this.tec.print(nameOrNot.right());
            return false;
        }
    }

    private boolean setCoordinatesFromInput(NumberFormat numberFormat, Consumer<Coordinates> setter) {
        var coords = new Coordinates();
        if (setValueFromInput("x", "coordinates.x", false, coords, coords::setX, (s) -> numberFormat.parse(s).floatValue())&&
                setValueFromInput("y", "coordinates.y", false, coords, coords::setY, Long::valueOf)) {
            setter.accept(coords);
            return true;
        }
        return false;
    }

    private <R, T extends  Exception> boolean setValueFromInput(String name, String query, boolean nullable, Object obj, Consumer<R> setter, UnsafeFunction<String, R, T> parser){
        var valueOrNot = this.tec.promptOrCancel("commandline.request_construct." + query,
                (s)->this.valueFromString(s, name, nullable, obj, parser));
        if(valueOrNot.isLeft()) {
            setter.accept(valueOrNot.left());
            return true;
        }
        tec.print(valueOrNot.right());
        return false;
    }

    private <R, T extends Exception> Either<R, String> valueFromString(String raw, String name, boolean nullable, Object obj, UnsafeFunction<String, R, T> parser){
        try{
            R value = null;
            if(!raw.isBlank())
                value = parser.apply(raw);

            var violations = this.validator.validateFieldValue(obj, obj.getClass().getDeclaredField(name), value);//TODO: this is bs, Validator has its own messaging shit
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

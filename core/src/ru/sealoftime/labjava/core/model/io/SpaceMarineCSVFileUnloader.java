package ru.sealoftime.labjava.core.model.io;

import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;

import java.util.function.Function;

public class SpaceMarineCSVFileUnloader extends CSVFileUnloader<SpaceMarine>{
    public SpaceMarineCSVFileUnloader(String fileName) {
        super(fileName, SpaceMarineCSVFileUnloader::deconstruct);
    }

    private static Object[] deconstruct(SpaceMarine sm) {
        return new Object[]{
                sm.getId(),
                sm.getName(),
                sm.getCoordinates().getX(),
                sm.getCoordinates().getY(),
                sm.getCreationDate(),
                sm.getHealth(),
                sm.getCategory(),
                sm.getWeaponType(),
                sm.getMeleeWeapon(),
                sm.getChapter().getName(),
                sm.getChapter().getMarinesCount()
        };
    }
}

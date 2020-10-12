package ru.sealoftime.labjava.core.model.data;

import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public interface DataProvider extends Collection<SpaceMarine> {
    /* @return the type of the dataprovider*/
    String getType();

    /* @return creation date of dataprovider. */
    Date getCreationDate();

    /* Deletes first element depending on concrete realisation. */
    int removeFirst();
}

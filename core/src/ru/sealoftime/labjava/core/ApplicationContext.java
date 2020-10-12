package ru.sealoftime.labjava.core;

import lombok.Data;
import ru.sealoftime.labjava.core.model.EventBus;
import ru.sealoftime.labjava.core.model.RequestExecutor;
import ru.sealoftime.labjava.core.model.data.DataProvider;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.io.FileLoader;
import ru.sealoftime.labjava.core.model.io.FileUnloader;

import java.util.Locale;
import java.util.ResourceBundle;

@Data
public class ApplicationContext {
    DataProvider dataProvider;
    FileLoader<SpaceMarine> fileLoader;
    FileUnloader<SpaceMarine> fileUnloader;

    RequestExecutor requestExecutor;
    EventBus eventBus;
    Locale currentLanguage;
    ResourceBundle currentLanguageBundle;
    Boolean isRunning;

}

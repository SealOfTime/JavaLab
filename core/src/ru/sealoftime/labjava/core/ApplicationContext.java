package ru.sealoftime.labjava.core;

import lombok.Data;
import ru.sealoftime.labjava.core.model.EventBus;
import ru.sealoftime.labjava.core.model.RequestExecutor;
import ru.sealoftime.labjava.core.model.data.DataProvider;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.io.DataLoader;
import ru.sealoftime.labjava.core.model.io.DataUnloader;
import ru.sealoftime.labjava.core.model.io.FileLoader;
import ru.sealoftime.labjava.core.model.io.FileUnloader;
import ru.sealoftime.labjava.core.view.LocalizationService;

import java.util.Locale;
import java.util.ResourceBundle;

@Data
public class ApplicationContext {
    DataProvider dataProvider;
    DataLoader<SpaceMarine> dataLoader;
    DataUnloader<SpaceMarine> dataUnloader;

    RequestExecutor requestExecutor;
    EventBus eventBus;
    LocalizationService localization;
    Boolean isRunning;

}

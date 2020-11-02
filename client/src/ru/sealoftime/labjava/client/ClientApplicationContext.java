package ru.sealoftime.labjava.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.view.UserInterface;
import ru.sealoftime.labjava.core.view.cli.CommandLineUserInterface;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientApplicationContext extends ApplicationContext {
    UserInterface ui;
}

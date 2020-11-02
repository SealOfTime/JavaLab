package ru.sealoftime.labjava.client.view;

import ru.sealoftime.labjava.core.view.LocalizationService;

import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ClientLocalizationService extends LocalizationService {
    private ResourceBundle client;
    public ClientLocalizationService(Locale locale) {
        super(locale);
        client = ResourceBundle.getBundle("client", locale);
    }

    @Override
    public String localize(String raw, Object... data) {
        if(client.containsKey(raw))
            return String.format(client.getString(raw), data);
        return super.localize(raw, data);
    }
}

package ru.sealoftime.labjava.core.view;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class LocalizationService {
    Locale locale;
    ResourceBundle bundle;

    public LocalizationService(Locale locale){
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle("commandline", locale);
    }

    public String localize(String raw, Object... data){
        String localized;
        try {
            var line = bundle.getString(raw);
            localized =  String.format(line, data);
        }catch(MissingResourceException e){
            localized = raw + " " + Arrays.deepToString(data);
        }
        return localized;
    }
}

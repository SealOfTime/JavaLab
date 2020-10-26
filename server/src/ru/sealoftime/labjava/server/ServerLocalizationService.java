package ru.sealoftime.labjava.server;

import lombok.Getter;
import lombok.Setter;
import ru.sealoftime.labjava.core.view.LocalizationService;

import java.util.Locale;
import java.util.ResourceBundle;

@Getter
@Setter
public class ServerLocalizationService extends LocalizationService {
    private ResourceBundle server;

    public ServerLocalizationService(Locale locale) {
        super(locale);
        this.server = ResourceBundle.getBundle("server", locale);
    }
    @Override
    public String localize(String raw, Object... data) {
        if(server.containsKey(raw)){
            return String.format(server.getString(raw), data);
        }
        return super.localize(raw, data);
    }

}

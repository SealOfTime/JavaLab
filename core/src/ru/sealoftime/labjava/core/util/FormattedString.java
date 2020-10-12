package ru.sealoftime.labjava.core.util;

import lombok.Value;

@Value
public class FormattedString {
    String line;
    Object[] data;

    public FormattedString(String line, Object... data){
        this.line = line;
        this.data = data;
    }
}

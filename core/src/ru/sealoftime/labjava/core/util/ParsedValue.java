package ru.sealoftime.labjava.core.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.NoSuchElementException;

@Getter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ParsedValue<T> {
    public boolean isPresent(){ return false; }
    public T value(){ throw new NoSuchElementException(); }

    public static <T> ParsedValue<T> from(T val){ return new PresentValue<>(val); }
    public static ParsedValue<?> undefined(){ return new ParsedValue<>(); }

    static class PresentValue<T> extends ParsedValue<T>{
        private final T value;

        private PresentValue(T value){
            this.value = value;
        }

        public T value(){
            return this.value;
        }
    }
}

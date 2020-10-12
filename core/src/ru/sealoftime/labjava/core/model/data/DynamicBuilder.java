package ru.sealoftime.labjava.core.model.data;

import lombok.*;

import lombok.experimental.FieldDefaults;
import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.util.UnsafeFunction;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Getter
public class DynamicBuilder<O> {

    private Set<Property<?>> properties;
    public DynamicBuilder(Property<?>... props){
        this.properties = Set.of(props);
    }
    public <T> DynamicBuilder<O> simple(String name, boolean nullable, BiConsumer<O, T> setter, UnsafeFunction<String, T, ? extends Exception> parser){
        this.properties.add(new SimpleProperty<T>(name, nullable, setter, parser));
        return this;
    }
    public <T> DynamicBuilder<O> complex(String name, boolean nullable, BiConsumer<O,T> setter, Supplier<T> dataSupplier, DynamicBuilder<T> builder){
        this.properties.add(new ComplexProperty<T>(name, nullable, setter, builder, dataSupplier));
        return this;
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
    public abstract class Property<T>{
        String name;
        boolean isComplex;
        boolean nullable;
        BiConsumer<O, T> setter;
    }

    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public class SimpleProperty<T> extends Property<T>{
        UnsafeFunction<String, T, ? extends Exception> parser;
        public SimpleProperty(String name, boolean nullable, BiConsumer<O, T> setter, UnsafeFunction<String, T, ? extends Exception> parser){
            super(name, false, nullable, setter);
            this.parser = parser;
        }
    }

    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public class ComplexProperty<T> extends Property<T>{
        DynamicBuilder<T> builder;
        Supplier<T> dataSupplier;
        public ComplexProperty(String name, boolean nullable, BiConsumer<O,T> setter, DynamicBuilder<T> builder, Supplier<T> dataSupplier){
            super(name, true, nullable, setter);
            this.builder = builder;
            this.dataSupplier = dataSupplier;
        }
    }
}

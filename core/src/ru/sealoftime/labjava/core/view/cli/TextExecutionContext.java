package ru.sealoftime.labjava.core.view.cli;

import ru.sealoftime.labjava.core.util.Either;

import java.util.function.Function;

public interface TextExecutionContext {
    void print(String rawOutput, Object... data);
    <T> Either<T, String> prompt(String query, Function<String, Either<T, String>> parser);
    <T> Either<T, String> promptOrCancel(String query, Function<String, Either<T, String>> parser);
}

package ru.sealoftime.labjava.core.util;

public interface UnsafeFunction<A, R, T extends Exception>{
    R apply(A t) throws T;
}

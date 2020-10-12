package ru.sealoftime.labjava.core.util;

import java.util.NoSuchElementException;

public interface Either<L, R> {

    static <L, R> Either<L, R> left(L l){
        return new Left<L, R>(l);
    }
    default L left(){
        throw new NoSuchElementException();
    }

    static <L, R> Either<L, R> right(R r){
        return new Right<L, R>(r);
    }
    default R right(){
        throw new NoSuchElementException();
    }

    default boolean isLeft(){ return false; }
    default boolean isRight(){ return false; }


    class Left<L, R> implements Either<L, R>{
        private final L value;

        private Left(L value){
            this.value = value;
        }

        @Override public boolean isLeft(){ return true; }
        @Override public L left(){ return this.value; }
    }
    class Right<L, R> implements Either<L, R>{
        private final R value;

        private Right(R value){
            this.value = value;
        }

        @Override public boolean isRight(){ return true; }
        @Override public R right(){ return this.value; }
    }
}
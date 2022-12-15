package com.heliorm.def;

public interface Where< O> {

    Where<O> and(Continuation<O> cont);

    Where<O> or(Continuation<O> cont);

}

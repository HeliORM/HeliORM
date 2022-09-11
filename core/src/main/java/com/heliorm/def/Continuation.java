package com.heliorm.def;

/**
 * @param <O> Type of the POJO
 * @author gideon
 */
public interface Continuation<O> {

    Continuation<O> and(Continuation<O> expr);

    Continuation<O> or(Continuation<O> expr);

}

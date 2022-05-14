package com.heliorm.def;

import com.heliorm.Table;

/**
 * @param <T> Type of the table
 * @param <O> Type of the POJO
 * @author gideon
 */
public interface Continuation<T extends Table<O>, O> {

    Continuation<T, O> and(Continuation<T, O> expr);

    Continuation<T, O> or(Continuation<T, O> expr);

}

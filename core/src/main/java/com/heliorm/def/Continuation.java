package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Continuation<T extends Table<O>, O> {

    Continuation<T, O> and(Continuation<T, O> expr);

    Continuation<T, O> or(Continuation<T, O> expr);

}

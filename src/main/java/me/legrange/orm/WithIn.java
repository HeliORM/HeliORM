package me.legrange.orm;

import java.util.List;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithIn<T extends Table<O>, O, C> {

    Continuation<T, O> in(List<C> values);

    Continuation<T, O> notIn(List<C> value);

    Continuation<T, O> in(C... values);

    Continuation<T, O> notIn(C... value);

}

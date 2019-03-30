package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithRange<T extends Table<O>, O, C> {

    Continuation<T, O> lt(C value);

    Continuation<T, O> le(C value);

    Continuation<T, O> gt(C value);

    Continuation<T, O> ge(C value);

}

package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithRange<T extends Table<O>, O, C, RT extends Table<RO>, RO> {

    Continuation<T, O, RT, RO> lt(C value);

    Continuation<T, O, RT, RO> le(C value);

    Continuation<T, O, RT, RO> gt(C value);

    Continuation<T, O, RT, RO> ge(C value);

}

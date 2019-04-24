package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithLike<T extends Table<O>, O, C, RT extends Table<RO>, RO> {

    Continuation<T, O, RT, RO> like(C value);

    Continuation<T, O, RT, RO> norLike(C value);

}

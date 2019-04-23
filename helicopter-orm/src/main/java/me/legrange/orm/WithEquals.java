package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithEquals<T extends Table<O>, O, C, RT extends Table<RO>, RO> {

    Continuation<T, O, RT, RO> eq(C value);

    Continuation<T, O, RT, RO> notEq(C value);

}

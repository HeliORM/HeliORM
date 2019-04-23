package me.legrange.orm;

import java.util.List;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithIn<T extends Table<O>, O, C, RT extends Table<RO>, RO> {

    Continuation<T, O, RT, RO> in(List<C> values);

    Continuation<T, O, RT, RO> notIn(List<C> value);

    Continuation<T, O, RT, RO> in(C... values);

    Continuation<T, O, RT, RO> notIn(C... value);

}

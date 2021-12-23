package com.heliorm.def;

import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithRange<T extends Table<O>, O, C> {

    Continuation<T, O> lt(C value) throws OrmException;

    Continuation<T, O> le(C value) throws OrmException;

    Continuation<T, O> gt(C value) throws OrmException;

    Continuation<T, O> ge(C value) throws OrmException;

}

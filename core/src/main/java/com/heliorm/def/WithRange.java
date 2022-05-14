package com.heliorm.def;

import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 * @author gideon
 */
public interface WithRange<T extends Table<O>, O, C> {

    Continuation<T, O> lt(C value) throws OrmException;

    Continuation<T, O> le(C value) throws OrmException;

    Continuation<T, O> gt(C value) throws OrmException;

    Continuation<T, O> ge(C value) throws OrmException;

}

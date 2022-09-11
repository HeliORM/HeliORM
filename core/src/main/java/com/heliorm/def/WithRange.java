package com.heliorm.def;

import com.heliorm.OrmException;

/**
 * @param <O> Object type
 * @param <C> Column/field type
 * @author gideon
 */
public interface WithRange<O, C> {

    Continuation<O> lt(C value) throws OrmException;

    Continuation<O> le(C value) throws OrmException;

    Continuation<O> gt(C value) throws OrmException;

    Continuation<O> ge(C value) throws OrmException;

}

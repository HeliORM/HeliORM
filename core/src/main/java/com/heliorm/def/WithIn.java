package com.heliorm.def;

import com.heliorm.OrmException;

import java.util.List;

/**
 * @param <O> Object type
 * @param <C> Column/field type
 * @author gideon
 */
public interface WithIn<O, C> {

    Continuation<O> in(List<C> values) throws OrmException;

    Continuation<O> notIn(List<C> value) throws OrmException;

    Continuation<O> in(C... values) throws OrmException;

    Continuation<O> notIn(C... value) throws OrmException;

}

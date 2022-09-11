package com.heliorm.def;

import com.heliorm.OrmException;

/**
 * @param <O> Object type
 * @param <C> Column/field type
 * @author gideon
 */
public interface WithEquals<O, C> {

    Continuation<O> eq(C value) throws OrmException;

    Continuation<O> notEq(C value) throws OrmException;


}

package com.heliorm.def;

import com.heliorm.OrmException;

/**
 * @param <O> Object type
 * @author gideon
 */
public interface WithIs<O> {

    Continuation<O> isNull() throws OrmException;

    Continuation<O> isNotNull() throws OrmException;


}

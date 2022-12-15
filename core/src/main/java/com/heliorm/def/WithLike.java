package com.heliorm.def;

import com.heliorm.OrmException;

/**
 * @param <O> Object type
 * @param <C> Column/field type
 * @author gideon
 */
public interface WithLike< O, C> {

    Continuation< O> like(C value) throws OrmException;

    Continuation< O> notLike(C value) throws OrmException;

}

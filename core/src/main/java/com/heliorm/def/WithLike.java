package com.heliorm.def;

import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 * @author gideon
 */
public interface WithLike<T extends Table<O>, O, C> {

    Continuation<T, O> like(C value) throws OrmException;

    Continuation<T, O> notLike(C value) throws OrmException;

}

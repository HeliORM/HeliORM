package com.heliorm.def;

import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 * @author gideon
 */
public interface WithIs<T extends Table<O>, O, C> {

    Continuation<T, O> isNull() throws OrmException;

    Continuation<T, O> isNotNull() throws OrmException;


}

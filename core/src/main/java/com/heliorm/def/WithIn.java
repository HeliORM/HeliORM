package com.heliorm.def;

import java.util.List;

import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithIn<T extends Table<O>, O, C> {

    Continuation<T, O> in(List<C> values) throws OrmException;

    Continuation<T, O> notIn(List<C> value) throws OrmException;

    Continuation<T, O> in(C... values) throws OrmException;

    Continuation<T, O> notIn(C... value) throws OrmException;

}

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
public interface WithIs<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> isNull() throws OrmException;

    ExpressionContinuation<T, O> isNotNull() throws OrmException;


}

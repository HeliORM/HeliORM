package com.heliorm.def;

import com.heliorm.OrmException;
import com.heliorm.Table;

import java.util.List;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithIn<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> in(List<C> values) throws OrmException;

    ExpressionContinuation<T, O> notIn(List<C> value) throws OrmException;

    ExpressionContinuation<T, O> in(C... values) throws OrmException;

    ExpressionContinuation<T, O> notIn(C... values) throws OrmException;

}

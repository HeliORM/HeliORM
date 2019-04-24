package me.legrange.orm;

import java.util.List;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithIn<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> in(List<C> values);

    ExpressionContinuation<T, O> notIn(List<C> value);

    ExpressionContinuation<T, O> in(C... values);

    ExpressionContinuation<T, O> notIn(C... value);

}

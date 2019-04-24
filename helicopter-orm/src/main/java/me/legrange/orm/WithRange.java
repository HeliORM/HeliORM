package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithRange<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> lt(C value);

    ExpressionContinuation<T, O> le(C value);

    ExpressionContinuation<T, O> gt(C value);

    ExpressionContinuation<T, O> ge(C value);

}

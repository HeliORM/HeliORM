package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithLike<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> like(C value);

    ExpressionContinuation<T, O> notLike(C value);

}

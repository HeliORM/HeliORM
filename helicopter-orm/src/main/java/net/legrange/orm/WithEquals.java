package net.legrange.orm;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithEquals<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> eq(C value) throws OrmException;

    ExpressionContinuation<T, O> notEq(C value) throws OrmException;

}

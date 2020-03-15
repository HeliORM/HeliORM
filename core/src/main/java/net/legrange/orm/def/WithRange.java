package net.legrange.orm.def;

import net.legrange.orm.OrmException;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 */
public interface WithRange<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> lt(C value) throws OrmException;

    ExpressionContinuation<T, O> le(C value) throws OrmException;

    ExpressionContinuation<T, O> gt(C value) throws OrmException;

    ExpressionContinuation<T, O> ge(C value) throws OrmException;

}

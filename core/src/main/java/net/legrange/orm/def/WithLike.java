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
public interface WithLike<T extends Table<O>, O, C> {

    ExpressionContinuation<T, O> like(C value) throws OrmException;

    ExpressionContinuation<T, O> notLike(C value) throws OrmException;

}

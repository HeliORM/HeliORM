package net.legrange.orm.def;

import net.legrange.orm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 *
 * @author gideon
 */
public interface EnumExpression<T extends Table<O>, O, C> extends Expression<T, O, C>,
        WithEquals<T, O, C>, WithIn<T, O, C> {

}

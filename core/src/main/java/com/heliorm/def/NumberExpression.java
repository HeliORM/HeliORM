package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 *
 * @author gideon
 */
public interface NumberExpression<T extends Table<O>, O, C> extends Expression<T, O, C>,
        WithRange<T, O, C>, WithEquals<T, O, C>, WithIn<T, O, C> {

}

package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface BooleanExpression<T extends Table<O>, O> extends
        Expression<T, O, Boolean>,
        WithEquals<T, O, Boolean>,
        WithIs<T,O, Boolean> {

}

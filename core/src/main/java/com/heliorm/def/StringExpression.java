package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface StringExpression<T extends Table<O>, O> extends
        Expression<T, O, String>,
        WithRange<T, O, String>,
        WithEquals<T, O, String>,
        WithIn<T, O, String>,
        WithLike<T, O, String> {

}

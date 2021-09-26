package com.heliorm.def;

import com.heliorm.Table;

/**
 * A field representing a short value
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface ShortField<T extends Table<O>, O> extends Field<T, O, Short>, Expression<T, O, Short>, WithRange<T, O, Short>, WithEquals<T, O, Short>, WithIn<T, O, Short>, WithIs<T, O, Short> {

}

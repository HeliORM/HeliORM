package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public interface ShortField<T extends Table<O>, O> extends Field<T, O, Short>, Expression<T, O, Short>, WithRange<T, O, Short>, WithEquals<T, O, Short>, WithIn<T, O, Short>, WithIs<T, O, Short> {

}

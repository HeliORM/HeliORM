package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public interface IntegerField<T extends Table<O>, O> extends Field<T, O, Integer>, Expression<T, O, Integer>, WithRange<T, O, Integer>, WithEquals<T, O, Integer>, WithIn<T, O, Integer>, WithIs<T, O, Integer> {

}

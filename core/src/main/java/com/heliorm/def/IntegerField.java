package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.Table;

/**
 * A field representing an int value
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface IntegerField<T extends Table<O>, O> extends Field<T, O, Integer>, WithRange<T, O, Integer>, WithEquals<T, O, Integer>, WithIn<T, O, Integer>, WithIs<T, O, Integer> {

}

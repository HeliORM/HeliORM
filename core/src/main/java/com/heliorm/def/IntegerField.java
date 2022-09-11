package com.heliorm.def;

import com.heliorm.Field;

/**
 * A field representing an int value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface IntegerField< O> extends Field<O, Integer>, WithRange<O, Integer>, WithEquals<O, Integer>, WithIn<O, Integer>, WithIs<O> {

}

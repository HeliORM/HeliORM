package com.heliorm.def;

import com.heliorm.Field;

/**
 * A field representing a short value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface ShortField<O> extends Field<O, Short>, WithRange<O, Short>, WithEquals<O, Short>, WithIn<O, Short>, WithIs<O> {

}

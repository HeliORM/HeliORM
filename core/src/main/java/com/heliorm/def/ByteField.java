package com.heliorm.def;

import com.heliorm.Field;

/**
 * A field representing a byte value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface ByteField< O> extends Field<O, Byte>, WithRange<O, Byte>, WithEquals<O, Byte>, WithIn<O, Byte>, WithIs<O> {

}

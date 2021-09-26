package com.heliorm.def;

import com.heliorm.Table;

/**
 * A field representing a byte value
 * 
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface ByteField<T extends Table<O>, O> extends Field<T, O, Byte>, Expression<T, O, Byte>, WithRange<T, O, Byte>, WithEquals<T, O, Byte>, WithIn<T, O, Byte>, WithIs<T, O, Byte> {

}

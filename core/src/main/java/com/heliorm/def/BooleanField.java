package com.heliorm.def;

import com.heliorm.Table;

/**
 * A field representing a boolean value
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface BooleanField<T extends Table<O>, O> extends Field<T, O, Boolean>, Expression<T, O, Boolean>, WithEquals<T, O, Boolean>, WithIs<T, O, Boolean> {

    @Override
   default FieldType getFieldType() {
        return FieldType.BOOLEAN;
    }
}

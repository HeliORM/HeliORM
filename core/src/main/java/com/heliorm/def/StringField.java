package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.Table;

/**
 * A field representing a String value
 *
 * @param <T> Table type
 * @param <O> Object type
 * @author gideon
 */
public interface StringField<T extends Table<O>, O> extends Field<T, O, String>, WithRange<T, O, String>, WithEquals<T, O, String>, WithIn<T, O, String>, WithLike<T, O, String>, WithIs<T, O, String> {

    @Override
    default FieldType getFieldType() {
        return FieldType.STRING;
    }


}

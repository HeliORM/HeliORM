package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.Table;

/**
 * A field representing a String value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface StringField< O> extends Field<O, String>, WithRange<O, String>, WithEquals<O, String>, WithIn<O, String>, WithLike<O, String>, WithIs<O> {

    @Override
    default FieldType getFieldType() {
        return FieldType.STRING;
    }


}

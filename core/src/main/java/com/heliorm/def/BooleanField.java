package com.heliorm.def;

import com.heliorm.Field;

/**
 * A field representing a boolean value
 *
 * @param <DO> Object type
 * @author gideon
 */
public interface BooleanField< DO> extends Field< DO, Boolean>, WithEquals<DO, Boolean>, WithIs<DO> {

    @Override
    default FieldType getFieldType() {
        return FieldType.BOOLEAN;
    }
}

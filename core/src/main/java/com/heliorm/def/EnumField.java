package com.heliorm.def;

import com.heliorm.Field;

/**
 * A field representing a enum value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface EnumField< O, C extends Enum<?>> extends Field<O, C>, WithEquals<O, C>, WithIn<O, C>, WithIs<O> {

    @Override
    default FieldType getFieldType() {
        return FieldType.ENUM;
    }


}

package com.heliorm.def;

import com.heliorm.Field;

/**
 * @param <T> Table type
 * @param <O> Object type
 * @author gideon
 */
public interface ListField< O, P> extends Field<O, P> {

    @Override
    default FieldType getFieldType() {
        return FieldType.LIST;
    }


}

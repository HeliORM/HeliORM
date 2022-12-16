package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.Table;

/**
 * @param <T> Table type
 * @param <O> Object type
 * @author gideon
 */
public interface SetField<O, P> extends Field<O, P> {

    @Override
    default FieldType getFieldType() {
        return FieldType.SET;
    }


}

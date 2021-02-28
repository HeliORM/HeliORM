package com.heliorm.def;

import com.heliorm.Table;

/**
 * @param <T> Table type
 * @param <O> Object type
 * @author gideon
 */
public interface SetField<T extends Table<O>, O, P> extends Field<T, O, P>, Expression<T, O, P> {

    @Override
    default FieldType getFieldType() {
        return FieldType.SET;
    }


}

package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface BooleanField<T extends Table<O>, O> extends Field<T, O, Boolean>, BooleanExpression<T, O> {

    @Override
   default FieldType getFieldType() {
        return FieldType.BOOLEAN;
    }
}

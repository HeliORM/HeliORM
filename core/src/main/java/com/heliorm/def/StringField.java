package com.heliorm.def;

import com.heliorm.Table;

/**
 * @param <T> Table type
 * @param <O> Object type
 * @author gideon
 */
public interface StringField<T extends Table<O>, O> extends Field<T, O, String>, StringExpression<T, O> {

    @Override
    default FieldType getFieldType() {
        return FieldType.STRING;
    }


}

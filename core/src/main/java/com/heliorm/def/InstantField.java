package com.heliorm.def;

import com.heliorm.Table;

import java.time.Instant;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface InstantField<T extends Table<O>, O> extends Field<T, O, Instant>, TimeExpresssion<T, O> {

    @Override
    public default FieldType getFieldType() {
        return FieldType.INSTANT;
    }
}

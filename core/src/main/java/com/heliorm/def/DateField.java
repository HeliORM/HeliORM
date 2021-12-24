package com.heliorm.def;

import com.heliorm.Table;

import java.util.Date;

/**
 * A field representing a Date value
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DateField<T extends Table<O>, O> extends Field<T, O, Date>, WithRange<T, O, Date>, WithEquals<T, O, Date>, WithIn<T, O, Date>, WithIs<T, O, Date> {

    @Override
   default FieldType getFieldType() {
        return FieldType.DATE;
    }
}

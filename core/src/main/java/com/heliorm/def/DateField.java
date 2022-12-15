package com.heliorm.def;

import com.heliorm.Field;

import java.util.Date;

/**
 * A field representing a Date value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface DateField< O> extends Field< O, Date>, WithRange<O, Date>, WithEquals<O, Date>, WithIn<O, Date>, WithIs<O> {

    @Override
    default FieldType getFieldType() {
        return FieldType.DATE;
    }
}

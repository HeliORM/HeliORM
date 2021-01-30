package com.heliorm.impl;

import com.heliorm.def.FloatField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class FloatFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Float> implements FloatField<T, O> {

    public FloatFieldPart(T table, String javaName) {
        super(table, FieldType.FLOAT, Float.class, javaName);
    }

}

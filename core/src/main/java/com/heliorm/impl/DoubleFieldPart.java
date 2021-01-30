package com.heliorm.impl;

import com.heliorm.def.DoubleField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class DoubleFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Double> implements DoubleField<T, O> {

    public DoubleFieldPart(T table, String javaName) {
        super(table, FieldType.DOUBLE, Double.class, javaName);
    }

}

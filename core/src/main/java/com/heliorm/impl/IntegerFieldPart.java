package com.heliorm.impl;

import com.heliorm.def.IntegerField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class IntegerFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Integer> implements IntegerField<T, O> {

    public IntegerFieldPart(T table, String javaName) {
        super(table, FieldType.INTEGER, Integer.class, javaName);
    }

}

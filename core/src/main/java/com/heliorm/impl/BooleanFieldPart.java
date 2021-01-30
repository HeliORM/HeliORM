package com.heliorm.impl;

import com.heliorm.def.BooleanField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class BooleanFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Boolean> implements
        BooleanField<T, O>,
        WithEqualsPart<T, O, Boolean>, WithIsPart<T, O, Boolean> {

    public BooleanFieldPart(T table, String javaName) {
        super(table, FieldType.BOOLEAN, Boolean.class, javaName);
    }

}

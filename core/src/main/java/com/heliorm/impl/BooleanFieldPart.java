package com.heliorm.impl;

import com.heliorm.def.BooleanField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class BooleanFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Boolean> implements
        BooleanField<T, O>,
        WithEqualsPart<T, O, Boolean> {

    public BooleanFieldPart(String javaName, String sqlName) {
        super(Boolean.class, javaName, sqlName);
    }

}

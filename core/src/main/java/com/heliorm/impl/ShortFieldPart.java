package com.heliorm.impl;

import com.heliorm.def.ShortField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class ShortFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Short> implements ShortField<T, O> {

    public ShortFieldPart(String javaName, String sqlName) {
        super(Short.class, javaName, sqlName);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.SHORT;
    }

}

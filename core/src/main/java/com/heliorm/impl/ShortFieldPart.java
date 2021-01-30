package com.heliorm.impl;

import com.heliorm.def.ShortField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class ShortFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Short> implements ShortField<T, O> {

    public ShortFieldPart(String javaName) {
        super(FieldType.SHORT, Short.class, javaName);
    }

}

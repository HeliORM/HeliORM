package com.heliorm.impl;

import com.heliorm.def.LongField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class LongFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Long> implements LongField<T, O> {

    public LongFieldPart(String javaName) {
        super(FieldType.LONG, Long.class, javaName);
    }

}

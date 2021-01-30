package com.heliorm.impl;

import com.heliorm.def.StringField;
import com.heliorm.Table;

/**
 * @author gideon
 */
public class StringFieldPart<T extends Table<O>, O> extends FieldPart<T, O, String> implements
        StringField<T, O>,
        WithEqualsPart<T, O, String>,
        WithRangePart<T, O, String>,
        WithInPart<T, O, String>,
        WithLikePart<T, O, String>,
        WithIsPart<T, O, String> {

    public StringFieldPart(String javaName) {
        super(FieldType.STRING, String.class, javaName);
    }

}

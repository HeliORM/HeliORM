package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

public final class StringValueExpressionPart<T extends Table<O>, O> extends ValueExpressionPart<T, O, String> {

    private String value;

    public StringValueExpressionPart(FieldPart left, Operator op, String value) {
        super(Field.FieldType.STRING, left, op);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}


package com.heliorm.impl;

import com.heliorm.Field;

public final class StringValueExpressionPart< O> extends ValueExpressionPart<O, String> {

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


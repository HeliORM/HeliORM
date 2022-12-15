package com.heliorm.impl;

import com.heliorm.Field;

public final class ShortValueExpressionPart<O> extends NumberValueExpressionPart< O, Short> {

    private final Short value;

    public ShortValueExpressionPart(ShortFieldPart<O> left, Operator op, Short value) {
        super(Field.FieldType.SHORT, left, op);
        this.value = value;
    }

    @Override
    public Short getValue() {
        return value;
    }

}


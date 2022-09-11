package com.heliorm.impl;

import com.heliorm.Field;

public final class IntegerValueExpressionPart<O> extends NumberValueExpressionPart<O, Integer> {

    private final Integer value;

    public IntegerValueExpressionPart(IntegerFieldPart<O> left, Operator op, Integer value) {
        super(Field.FieldType.INTEGER, left, op);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }


}


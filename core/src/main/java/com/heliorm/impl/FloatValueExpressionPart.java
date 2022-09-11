package com.heliorm.impl;

import com.heliorm.Field;

public final class FloatValueExpressionPart<O> extends NumberValueExpressionPart<O, Float> {

    private final Float value;

    public FloatValueExpressionPart(FloatFieldPart<O> left, Operator op, Float value) {
        super(Field.FieldType.FLOAT, left, op);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }


}


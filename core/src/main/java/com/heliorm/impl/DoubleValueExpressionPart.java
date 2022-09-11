package com.heliorm.impl;

import com.heliorm.Field;

public final class DoubleValueExpressionPart<O> extends NumberValueExpressionPart< O, Double> {

    private final Double value;

    public DoubleValueExpressionPart(DoubleFieldPart<O> left, Operator op, Double value) {
        super(Field.FieldType.DOUBLE, left, op);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }


}


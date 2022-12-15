package com.heliorm.impl;

import com.heliorm.Field;

public abstract class NumberValueExpressionPart<O, N extends Number> extends ValueExpressionPart<O, N> {

    protected NumberValueExpressionPart(Field.FieldType fieldType, NumberFieldPart left, Operator op) {
        super(fieldType, left, op);
    }
}


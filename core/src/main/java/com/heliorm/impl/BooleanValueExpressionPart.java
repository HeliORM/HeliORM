package com.heliorm.impl;

import com.heliorm.Field;

public final class BooleanValueExpressionPart<O> extends ValueExpressionPart<O, Boolean> {

    private final Boolean value;

    public BooleanValueExpressionPart(FieldPart left, Operator op, Boolean value) {
        super(Field.FieldType.BOOLEAN, left, op);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }


}


package com.heliorm.impl;

import com.heliorm.Field;

public final class LongValueExpressionPart<O> extends NumberValueExpressionPart< O, Long> {

    private final Long value;

    public LongValueExpressionPart(LongFieldPart<O> left, Operator op, Long value) {
        super(Field.FieldType.LONG, left, op);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }


}


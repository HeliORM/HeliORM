package com.heliorm.impl;

import com.heliorm.Field;

import java.time.Instant;

public final class InstantValueExpressionPart<O> extends ValueExpressionPart< O, Instant> {

    private final Instant value;

    public InstantValueExpressionPart(InstantFieldPart left, Operator op, Instant value) {
        super(Field.FieldType.INSTANT, left, op);
        this.value = value;
    }

    @Override
    public Instant getValue() {
        return value;
    }

}


package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

import java.time.Instant;

public final class InstantValueExpressionPart<T extends Table<O>, O> extends ValueExpressionPart<T,O, Instant> {

    private Instant value;

    public InstantValueExpressionPart(InstantFieldPart left, Operator op, Instant value) {
        super(Field.FieldType.INSTANT, left, op);
        this.value = value;
    }

    @Override
    public Instant getValue() {
        return value;
    }

}


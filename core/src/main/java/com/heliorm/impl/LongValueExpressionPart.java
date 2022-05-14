package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

public final class LongValueExpressionPart<T extends Table<O>, O> extends NumberValueExpressionPart<T, O, Long> {

    private Long value;

    public LongValueExpressionPart(LongFieldPart<T, O> left, Operator op, Long value) {
        super(Field.FieldType.LONG, left, op);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }


}


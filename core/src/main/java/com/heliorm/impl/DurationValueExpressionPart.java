package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

import java.time.Duration;

public final class DurationValueExpressionPart<T extends Table<O>, O> extends ValueExpressionPart<T,O, Duration> {

    private Duration value;

    public DurationValueExpressionPart(FieldPart left, Operator op, Duration value) {
        super(Field.FieldType.DURATION, left, op);
        this.value = value;
    }

    @Override
    public Duration getValue() {
        return value;
    }

   


}


package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.Field;

public final class FloatValueExpressionPart<T extends Table<O>, O> extends NumberValueExpressionPart<T,O, Float> {

    private Float value;

    public FloatValueExpressionPart(FloatFieldPart<T,O> left, Operator op, Float value) {
        super(Field.FieldType.FLOAT, left, op);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }

   


}


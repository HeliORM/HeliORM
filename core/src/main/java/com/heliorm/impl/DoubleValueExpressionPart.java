package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.Field;

public final class DoubleValueExpressionPart<T extends Table<O>, O> extends NumberValueExpressionPart<T,O, Double> {

    private Double value;

    public DoubleValueExpressionPart(DoubleFieldPart<T,O> left, Operator op, Double value) {
        super(Field.FieldType.DOUBLE, left, op);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

   


}


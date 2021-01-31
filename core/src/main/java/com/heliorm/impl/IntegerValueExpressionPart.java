package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

public final class IntegerValueExpressionPart<T extends Table<O>, O> extends NumberValueExpressionPart<T,O, Integer> {

    private Integer value;

    public IntegerValueExpressionPart(IntegerFieldPart<T,O> left, Operator op, Integer value) {
        super(Field.FieldType.INTEGER, left, op);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

   


}


package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

public final class BooleanValueExpressionPart<T extends Table<O>, O> extends ValueExpressionPart<T, O, Boolean> {

    private Boolean value;

    public BooleanValueExpressionPart(FieldPart left, Operator op, Boolean value) {
        super(Field.FieldType.BOOLEAN, left, op);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }


}


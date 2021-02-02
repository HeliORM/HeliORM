package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

public abstract class NumberValueExpressionPart<T extends Table<O>, O, N extends Number> extends ValueExpressionPart<T,O, N> {

    protected NumberValueExpressionPart(Field.FieldType fieldType, NumberFieldPart left, Operator op) {
        super(fieldType, left, op);
    }
}


package com.heliorm.impl;

import com.heliorm.Field;

public abstract class NumberListExpressionPart< O, C extends Number> extends ListExpressionPart<O, C> {

    protected NumberListExpressionPart(Field.FieldType dataType, NumberFieldPart left, Operator op) {
        super(dataType, left, op);
    }

}

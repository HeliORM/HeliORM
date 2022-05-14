package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

public abstract class NumberListExpressionPart<T extends Table<O>, O, C extends Number> extends ListExpressionPart<T, O, C> {

    protected NumberListExpressionPart(Field.FieldType dataType, NumberFieldPart left, Operator op) {
        super(dataType, left, op);
    }

}

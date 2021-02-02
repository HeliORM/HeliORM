package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

public abstract class NumberListExpressionPart<T extends Table<O>,O, C extends Number> extends ListExpressionPart<T,O,C> {

    protected NumberListExpressionPart(Field.FieldType dataType, NumberFieldPart left, Operator op) {
        super(dataType, left, op);
    }

}

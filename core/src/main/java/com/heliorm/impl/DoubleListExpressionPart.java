package com.heliorm.impl;

import com.heliorm.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DoubleListExpressionPart<O> extends NumberListExpressionPart<O, Double> {

    private final List<Double> values;

    public DoubleListExpressionPart(DoubleFieldPart left, Operator op, Collection<Byte> values) {
        super(Field.FieldType.DOUBLE, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List getValues() {
        return values;
    }
}

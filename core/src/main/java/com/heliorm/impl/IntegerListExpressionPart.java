package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class IntegerListExpressionPart<T extends Table<O>, O> extends NumberListExpressionPart<T, O, Integer> {

    private final List<Integer> values;

    public IntegerListExpressionPart(IntegerFieldPart left, Operator op, Collection<Byte> values) {
        super(Field.FieldType.INTEGER, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List getValues() {
        return values;
    }
}

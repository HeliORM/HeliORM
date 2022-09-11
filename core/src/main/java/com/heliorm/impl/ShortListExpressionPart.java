package com.heliorm.impl;

import com.heliorm.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ShortListExpressionPart< O> extends NumberListExpressionPart<O, Short> {

    private final List<Short> values;

    public ShortListExpressionPart(ShortFieldPart left, Operator op, Collection<Short> values) {
        super(Field.FieldType.SHORT, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List getValues() {
        return values;
    }
}

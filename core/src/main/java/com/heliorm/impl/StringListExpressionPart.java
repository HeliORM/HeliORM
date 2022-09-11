package com.heliorm.impl;

import com.heliorm.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class StringListExpressionPart<O> extends ListExpressionPart<O, String> {

    private final List<String> values;

    public StringListExpressionPart(FieldPart left, Operator op, Collection<String> values) {
        super(Field.FieldType.STRING, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List<String> getValues() {
        return values;
    }
}

package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class StringListExpressionPart<T extends Table<O>,O> extends ListExpressionPart<T,O, String> {

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

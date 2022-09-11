package com.heliorm.impl;

import com.heliorm.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class InstantListExpressionPart< O> extends ListExpressionPart< O, Instant> {

    private final List<Instant> values;

    public InstantListExpressionPart(FieldPart left, Operator op, Collection<Instant> values) {
        super(Field.FieldType.INSTANT, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List<Instant> getValues() {
        return values;
    }
}

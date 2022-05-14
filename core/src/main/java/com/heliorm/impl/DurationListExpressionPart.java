package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DurationListExpressionPart<T extends Table<O>, O> extends ListExpressionPart<T, O, Duration> {

    private final List<Duration> values;

    public DurationListExpressionPart(FieldPart left, Operator op, Collection<Duration> values) {
        super(Field.FieldType.DURATION, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List<Duration> getValues() {
        return values;
    }
}

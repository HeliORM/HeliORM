package com.heliorm.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class EnumListExpressionPart<O, E extends Enum<E>> extends ListExpressionPart<O, E> {

    private final List<E> values;

    public EnumListExpressionPart(FieldPart left, Operator op, Collection<Duration> values) {
        super(left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List<E> getValues() {
        return values;
    }
}

package com.heliorm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public final class DateListExpressionPart< O> extends ListExpressionPart<O, Date> {

    private final List<Date> values;

    public DateListExpressionPart(FieldPart left, Operator op, Collection<Date> values) {
        super(left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List<Date> getValues() {
        return values;
    }
}

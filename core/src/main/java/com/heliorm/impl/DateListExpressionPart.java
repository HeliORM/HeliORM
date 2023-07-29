package com.heliorm.impl;

import java.util.Collection;
import java.util.Date;

public final class DateListExpressionPart< O> extends ListExpressionPart<O, Date> {


    public DateListExpressionPart(FieldPart<O, Date> left, Operator op, Collection<Date> values) {
        super(left, op, values);
    }

}

package com.heliorm.impl;

import java.util.Collection;

public abstract class NumberListExpressionPart<O, C extends Number> extends ListExpressionPart<O, C> {
    protected NumberListExpressionPart(NumberFieldPart<O, C> left, Operator op, Collection<C> values) {
        super(left, op, values);
    }
}

package com.heliorm.impl;

import java.util.Collection;

public final class DoubleListExpressionPart<O> extends NumberListExpressionPart<O, Double> {

    public DoubleListExpressionPart(DoubleFieldPart<O> left, Operator op, Collection<Double> values) {
        super(left, op, values);
    }

}

package com.heliorm.impl;

import java.util.Collection;

public final class FloatListExpressionPart< O> extends NumberListExpressionPart<O, Float> {

    public FloatListExpressionPart(FloatFieldPart<O> left, Operator op, Collection<Float> values) {
        super(left, op,values);
    }

}

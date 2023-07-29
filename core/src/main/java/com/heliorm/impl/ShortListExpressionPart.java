package com.heliorm.impl;

import java.util.Collection;

public final class ShortListExpressionPart<O> extends NumberListExpressionPart<O, Short> {
    public ShortListExpressionPart(ShortFieldPart<O> left, Operator op, Collection<Short> values) {
        super(left, op, values);
    }


}

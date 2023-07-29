package com.heliorm.impl;

import com.heliorm.Table;

import java.util.Collection;

public final class IntegerListExpressionPart<T extends Table<O>, O> extends NumberListExpressionPart<O, Integer> {

    public IntegerListExpressionPart(IntegerFieldPart<O> left, Operator op, Collection<Integer> values) {
        super(left, op, values);
    }

}

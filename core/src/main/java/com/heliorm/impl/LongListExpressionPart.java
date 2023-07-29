package com.heliorm.impl;

import com.heliorm.Table;

import java.util.Collection;

public final class LongListExpressionPart<T extends Table<O>, O> extends NumberListExpressionPart<O, Long> {


    public LongListExpressionPart(LongFieldPart<O> left, Operator op, Collection<Long> values) {
        super(left, op, values);
    }

}

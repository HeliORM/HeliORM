package com.heliorm.impl;

import java.util.Collection;

public final class ByteListExpressionPart<O> extends NumberListExpressionPart<O, Byte> {

    public ByteListExpressionPart(ByteFieldPart<O> left, Operator op, Collection<Byte> values) {
        super(left, op, values);
    }

}

package com.heliorm.impl;

import java.util.Collection;

public final class EnumListExpressionPart<O, E extends Enum<E>> extends ListExpressionPart<O, E> {


    public EnumListExpressionPart(FieldPart<O,E> left, Operator op, Collection<E> values) {
        super(left, op,values);
    }
}

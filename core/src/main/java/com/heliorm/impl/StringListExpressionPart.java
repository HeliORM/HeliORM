package com.heliorm.impl;

import java.util.Collection;

public final class StringListExpressionPart<O> extends ListExpressionPart<O, String> {

    public StringListExpressionPart(FieldPart left, Operator op, Collection<String> values) {
        super(left, op, values);
    }

}

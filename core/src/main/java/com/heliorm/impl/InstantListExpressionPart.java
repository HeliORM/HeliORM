package com.heliorm.impl;

import java.time.Instant;
import java.util.Collection;

public final class InstantListExpressionPart< O> extends ListExpressionPart< O, Instant> {

    public InstantListExpressionPart(FieldPart<O,Instant> left, Operator op, Collection<Instant> values) {
        super(left, op, values);
    }


}

package com.heliorm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

/**
 * @author gideon
 */
public abstract class ListExpressionPart< O, C> extends ExpressionPart<O, C> {

    private final Operator operator;
    private final List<C> values;

    protected ListExpressionPart(FieldPart<O,C> left, Operator op, Collection<C> values ) {
        super(Type.LIST_EXPRESSION, left);
        this.operator = op;
        this.values = new ArrayList<>(values);
    }

    public Operator getOperator() {
        return operator;
    }

    public final List<C> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return format("%s (%s)", operator.name(), getValues());
    }

    public enum Operator {
        IN, NOT_IN
    }
}

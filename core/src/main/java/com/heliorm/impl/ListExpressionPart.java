package com.heliorm.impl;

import java.util.List;

import static java.lang.String.format;

/**
 * @author gideon
 */
public abstract class ListExpressionPart< O, C> extends ExpressionPart<O, C> {

    private final Operator operator;

    protected ListExpressionPart(FieldPart<O,C> left, Operator op) {
        super(Type.LIST_EXPRESSION, left);
        this.operator = op;
    }

    public Operator getOperator() {
        return operator;
    }

    public abstract List<C> getValues();

    @Override
    public String toString() {
        return format("%s (%s)", operator.name(), getValues());
    }

    public enum Operator {
        IN, NOT_IN
    }
}

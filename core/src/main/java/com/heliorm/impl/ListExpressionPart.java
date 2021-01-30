package com.heliorm.impl;

import com.heliorm.Table;

import java.util.List;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class ListExpressionPart<T extends Table<O>, O, C> extends ExpressionPart<T, O, C> {

    private final Operator operator;
    private final List<C> values;

    public enum Operator {
        IN, NOT_IN;
    }

    public ListExpressionPart(FieldPart left, Operator op, List<C> values) {
        super(Type.LIST_EXPRESSION, left);
        this.operator = op;
        this.values = values;
    }

    public Operator getOperator() {
        return operator;
    }

    public List<C> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return format("%s (%s)", operator.name(), values);
    }
}

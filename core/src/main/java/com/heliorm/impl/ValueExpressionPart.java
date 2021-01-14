package com.heliorm.impl;

import com.heliorm.Table;

import static java.lang.String.format;

/**
 *
 * @author gideon
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * */
public class ValueExpressionPart<T extends Table<O>, O, C> extends ExpressionPart<T, O, C> {

    private final Operator operator;
    private final C value;

    public enum Operator {
        EQ, NOT_EQ, LT, LE, GT, GE, LIKE, NOT_LIKE;
    }

    public ValueExpressionPart(FieldPart left, Operator op, C value) {
        super(Type.VALUE_EXPRESSION, left);
        this.operator = op;
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public C getValue() {
        return value;
    }

    @Override
    public String toString() {
        return format("%s '%s'", operator.name(), value);
    }

}

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
public class IsExpressionPart<T extends Table<O>, O, C> extends ExpressionPart<T, O, C> {

    private final Operator operator;

    public enum Operator {
        IS_NULL, IS_NOT_NULL;
    }

    public IsExpressionPart(FieldPart left, Operator op) {
        super(left);
        this.operator = op;
    }

    @Override
    public Type getType() {
        return Type.IS_EXPRESSION;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return format("%s", operator.name());
    }

}

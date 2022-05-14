package com.heliorm.impl;

import com.heliorm.Table;

import static java.lang.String.format;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public class IsExpressionPart<T extends Table<O>, O, C> extends ExpressionPart<T, O, C> {

    private final Operator operator;

    public IsExpressionPart(FieldPart left, Operator op) {
        super(Type.IS_EXPRESSION, left);
        this.operator = op;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return format("%s", operator.name());
    }

    public enum Operator {
        IS_NULL, IS_NOT_NULL;
    }

}

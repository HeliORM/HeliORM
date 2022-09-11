package com.heliorm.impl;

import com.heliorm.Field;

import static java.lang.String.format;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public abstract class ValueExpressionPart< O, C> extends ExpressionPart<O, C> {

    private final Field.FieldType dataType;
    private final Operator operator;

    protected ValueExpressionPart(Field.FieldType dataType, FieldPart left, Operator op) {
        super(Type.VALUE_EXPRESSION, left);
        this.dataType = dataType;
        this.operator = op;
    }

    public final Operator getOperator() {
        return operator;
    }

    public final Field.FieldType getDataType() {
        return dataType;
    }

    public abstract C getValue();

    @Override
    public final String toString() {
        return format("%s '%s'", operator.name(), getValue());
    }

    public enum Operator {
        EQ, NOT_EQ, LT, LE, GT, GE, LIKE, NOT_LIKE;
    }

}

package com.heliorm.impl;

import com.heliorm.Field;

import static java.lang.String.format;

/**
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public final class ValueExpressionPart<O, C> extends ExpressionPart<O, C> {

    private final Field.FieldType dataType;
    private final Operator operator;

    private final C value;

    ValueExpressionPart(Field.FieldType dataType, FieldPart<O, C> left, Operator op, C value) {
        super(Type.VALUE_EXPRESSION, left);
        this.dataType = dataType;
        this.operator = op;
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public Field.FieldType getDataType() {
        return dataType;
    }

    public C getValue() {
        return value;
    }

    @Override
    public String toString() {
        return format("%s '%s'", operator.name(), getValue());
    }

    public enum Operator {
        EQ, NOT_EQ, LT, LE, GT, GE, LIKE, NOT_LIKE
    }

}

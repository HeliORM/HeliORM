package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

import static java.lang.String.format;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public abstract class ValueExpressionPart<T extends Table<O>, O, C> extends ExpressionPart<T, O, C> {

    private final Field.FieldType dataType;
    private final Operator operator;

    public enum Operator {
        EQ, NOT_EQ, LT, LE, GT, GE, LIKE, NOT_LIKE;
    }

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
    public String toString() {
        return format("%s '%s'", operator.name(), getValue());
    }

}

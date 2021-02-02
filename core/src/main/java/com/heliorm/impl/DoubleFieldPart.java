package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.DoubleField;
import com.heliorm.def.ExpressionContinuation;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gideon
 */
public final class DoubleFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Double> implements DoubleField<T, O> {

    public DoubleFieldPart(T table, String javaName) {
        super(table, FieldType.DOUBLE, Double.class, javaName);
    }

    @Override
    public ExpressionContinuation<T, O> eq(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public ExpressionContinuation<T, O> lt(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public ExpressionContinuation<T, O> isNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public ExpressionContinuation<T, O> isNotNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

    @Override
    public ExpressionContinuation<T, O> in(List<Double> values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public ExpressionContinuation<T, O> notIn(List<Double> values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public ExpressionContinuation<T, O> in(Double... values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public ExpressionContinuation<T, O> notIn(Double... values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

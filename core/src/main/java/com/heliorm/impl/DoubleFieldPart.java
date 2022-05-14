package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.DoubleField;

import java.util.Arrays;
import java.util.List;

/**
 * @author gideon
 */
public final class DoubleFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Double> implements DoubleField<T, O> {

    public DoubleFieldPart(T table, String javaName) {
        super(table, FieldType.DOUBLE, Double.class, javaName);
    }

    @Override
    public Continuation<T, O> eq(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<T, O> lt(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public Continuation<T, O> isNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public Continuation<T, O> isNotNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

    @Override
    public Continuation<T, O> in(List<Double> values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<Double> values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(Double... values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(Double... values) throws OrmException {
        return new DoubleListExpressionPart((DoubleFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

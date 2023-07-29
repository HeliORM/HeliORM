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
public final class DoubleFieldPart<O> extends NumberFieldPart<O, Double> implements DoubleField<O> {

    public DoubleFieldPart(Table<O> table, String javaName) {
        super(table, FieldType.DOUBLE, Double.class, javaName);
    }

    @Override
    public Continuation<O> eq(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<O> lt(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Double value) throws OrmException {
        return new DoubleValueExpressionPart((DoubleFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public Continuation<O> isNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public Continuation<O> isNotNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

    @Override
    public Continuation<O> in(List<Double> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Double> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Double... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Double... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

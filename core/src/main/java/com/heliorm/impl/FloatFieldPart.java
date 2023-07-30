package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.FloatField;

import java.util.Arrays;
import java.util.List;

/**
 * @author gideon
 */
public class FloatFieldPart<O> extends NumberFieldPart<O, Float> implements FloatField<O> {

    public FloatFieldPart(Table<O> table, String javaName) {
        super(table, FieldType.FLOAT, Float.class, javaName);
    }


    @Override
    public Continuation<O> eq(Float value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Float value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<O> lt(Float value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Float value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Float value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Float value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public Continuation<O> isNull() throws OrmException {
        return new IsExpressionPart<>(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public Continuation<O> isNotNull() throws OrmException {
        return new IsExpressionPart<>(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

    @Override
    public Continuation<O> in(List<Float> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Float> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Float... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Float... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

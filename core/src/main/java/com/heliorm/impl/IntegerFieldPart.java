package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.IntegerField;

import java.util.Arrays;
import java.util.List;

import static com.heliorm.Field.FieldType.INTEGER;

/**
 * @author gideon
 */
public class IntegerFieldPart<O> extends NumberFieldPart<O, Integer> implements IntegerField<O> {

    public IntegerFieldPart(Table<O> table, String javaName) {
        super(table, INTEGER, Integer.class, javaName);
    }


    @Override
    public Continuation<O> eq(Integer value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Integer value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<O> lt(Integer value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Integer value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Integer value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Integer value) throws OrmException {
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
    public Continuation<O> in(List<Integer> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Integer> values) throws OrmException {
        return new ListExpressionPart<>( getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Integer... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Integer... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

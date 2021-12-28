package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.IntegerField;
import com.heliorm.Table;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gideon
 */
public class IntegerFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Integer> implements IntegerField<T, O> {

    public IntegerFieldPart(T table, String javaName) {
        super(table, FieldType.INTEGER, Integer.class, javaName);
    }


    @Override
    public Continuation<T, O> eq(Integer value) throws OrmException {
        return new IntegerValueExpressionPart((IntegerFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Integer value) throws OrmException {
        return new IntegerValueExpressionPart((IntegerFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<T, O> lt(Integer value) throws OrmException {
        return new IntegerValueExpressionPart((IntegerFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(Integer value) throws OrmException {
        return new IntegerValueExpressionPart((IntegerFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(Integer value) throws OrmException {
        return new IntegerValueExpressionPart((IntegerFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(Integer value) throws OrmException {
        return new IntegerValueExpressionPart((IntegerFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<T, O> in(List<Integer> values) throws OrmException {
        return new IntegerListExpressionPart((IntegerFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<Integer> values) throws OrmException {
        return new IntegerListExpressionPart((IntegerFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(Integer... values) throws OrmException {
        return new IntegerListExpressionPart((IntegerFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(Integer... values) throws OrmException {
        return new IntegerListExpressionPart((IntegerFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

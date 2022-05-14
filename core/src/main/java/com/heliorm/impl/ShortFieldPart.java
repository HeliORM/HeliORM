package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.ShortField;

import java.util.Arrays;
import java.util.List;

/**
 * @author gideon
 */
public class ShortFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Short> implements ShortField<T, O> {

    public ShortFieldPart(T table, String javaName) {
        super(table, FieldType.SHORT, Short.class, javaName);
    }

    @Override
    public Continuation<T, O> eq(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O> lt(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<T, O> in(List<Short> values) throws OrmException {
        return new ShortListExpressionPart((ShortFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<Short> values) throws OrmException {
        return new ShortListExpressionPart((ShortFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(Short... values) throws OrmException {
        return new ShortListExpressionPart((ShortFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(Short... values) throws OrmException {
        return new ShortListExpressionPart((ShortFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

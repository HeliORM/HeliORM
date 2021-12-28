package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.StringField;
import com.heliorm.Table;

import java.util.Arrays;
import java.util.List;

/**
 * @author gideon
 */
public class StringFieldPart<T extends Table<O>, O> extends FieldPart<T, O, String> implements
        StringField<T, O>,
        WithEqualsPart<T, O, String>,
        WithRangePart<T, O, String>,
        WithInPart<T, O, String>,
        WithLikePart<T, O, String>,
        WithIsPart<T, O, String> {

    public StringFieldPart(T table, String javaName) {
        super(table, FieldType.STRING, String.class, javaName);
    }

    @Override
    public Continuation<T, O> eq(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<T, O> lt(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public Continuation<T, O> like(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.LIKE, value);
    }

    @Override
    public Continuation<T, O> notLike(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_LIKE, value);
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
    public Continuation<T, O> in(List<String> values) throws OrmException {
        return new StringListExpressionPart((StringFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<String> values) throws OrmException {
        return new StringListExpressionPart((StringFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(String... values) throws OrmException {
        return new StringListExpressionPart((StringFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(String... values) throws OrmException {
        return new StringListExpressionPart((StringFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

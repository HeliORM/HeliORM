package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.StringField;

import java.util.Arrays;
import java.util.List;

/**
 * @author gideon
 */
public class StringFieldPart<O> extends FieldPart<O, String> implements
        StringField<O>,
        WithEqualsPart<O, String>,
        WithRangePart<O, String>,
        WithInPart<O, String>,
        WithLikePart<O, String>,
        WithIsPart<O, String> {

    public StringFieldPart(Table<O> table, String javaName) {
        super(table, FieldType.STRING, String.class, javaName);
    }

    @Override
    public Continuation<O> eq(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<O> lt(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public Continuation<O> like(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.LIKE, value);
    }

    @Override
    public Continuation<O> notLike(String value) throws OrmException {
        return new StringValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_LIKE, value);
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
    public Continuation<O> in(List<String> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<String> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(String... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(String... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

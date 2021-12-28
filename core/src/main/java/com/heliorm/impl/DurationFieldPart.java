package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.DurationField;
import com.heliorm.Table;
import com.heliorm.def.Continuation;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gideon
 */
public class DurationFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Duration> implements
        DurationField<T, O>,
        WithRangePart<T, O, Duration>,
        WithEqualsPart<T, O, Duration>,
        WithInPart<T, O, Duration>, WithIsPart<T, O, Duration> {

    public DurationFieldPart(T table, String javaName) {
        super(table, FieldType.DURATION, Duration.class, javaName);
    }


    @Override
    public Continuation<T, O> eq(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<T, O> lt(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<T, O> in(List<Duration> values) throws OrmException {
        return new DurationListExpressionPart((DurationFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<Duration> values) throws OrmException {
        return new DurationListExpressionPart((DurationFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(Duration... values) throws OrmException {
        return new DurationListExpressionPart((DurationFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(Duration... values) throws OrmException {
        return new DurationListExpressionPart((DurationFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.DurationField;
import com.heliorm.Table;
import com.heliorm.def.ExpressionContinuation;

import java.time.Duration;

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
    public ExpressionContinuation<T, O> eq(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public ExpressionContinuation<T, O> lt(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Duration value) throws OrmException {
        return new DurationValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public ExpressionContinuation<T, O> isNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public ExpressionContinuation<T, O> isNotNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

}

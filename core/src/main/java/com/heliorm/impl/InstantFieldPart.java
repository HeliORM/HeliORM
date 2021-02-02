package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.InstantField;
import com.heliorm.Table;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gideon
 */
public final class InstantFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Instant> implements
        InstantField<T, O>,
        WithRangePart<T, O, Instant>,
        WithEqualsPart<T, O, Instant>,
        WithInPart<T, O, Instant>, WithIsPart<T,O,Instant> {

    public InstantFieldPart(T table, String javaName) {
        super(table, FieldType.INSTANT, Instant.class, javaName);
    }


    @Override
    public ExpressionContinuation<T, O> eq(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> lt(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public ExpressionContinuation<T, O> isNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public ExpressionContinuation<T, O> isNotNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

    @Override
    public ExpressionContinuation<T, O> in(List<Instant> values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public ExpressionContinuation<T, O> notIn(List<Instant> values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public ExpressionContinuation<T, O> in(Instant... values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public ExpressionContinuation<T, O> notIn(Instant... values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

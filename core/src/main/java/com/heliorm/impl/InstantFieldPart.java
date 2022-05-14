package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.InstantField;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * @author gideon
 */
public final class InstantFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Instant> implements
        InstantField<T, O>,
        WithRangePart<T, O, Instant>,
        WithEqualsPart<T, O, Instant>,
        WithInPart<T, O, Instant>, WithIsPart<T, O, Instant> {

    public InstantFieldPart(T table, String javaName) {
        super(table, FieldType.INSTANT, Instant.class, javaName);
    }


    @Override
    public Continuation<T, O> eq(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O> lt(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(Instant value) throws OrmException {
        return new InstantValueExpressionPart((InstantFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<T, O> in(List<Instant> values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<Instant> values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(Instant... values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(Instant... values) throws OrmException {
        return new InstantListExpressionPart((InstantFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

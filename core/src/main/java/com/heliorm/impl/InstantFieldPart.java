package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.InstantField;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.heliorm.Field.FieldType.INSTANT;

/**
 * @author gideon
 */
public final class InstantFieldPart<O> extends FieldPart<O, Instant> implements InstantField<O>,
        WithRangePart<O, Instant>,
        WithEqualsPart<O, Instant>,
        WithInPart<O, Instant>, WithIsPart<O, Instant> {

    public InstantFieldPart(Table<O> table, String javaName) {
        super(table, INSTANT, Instant.class, javaName);
    }


    @Override
    public Continuation<O> eq(Instant value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Instant value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<O> lt(Instant value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Instant value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Instant value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Instant value) throws OrmException {
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
    public Continuation<O> in(List<Instant> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Instant> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Instant... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Instant... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

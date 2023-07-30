package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.LongField;

import java.util.Arrays;
import java.util.List;

import static com.heliorm.Field.FieldType.LONG;

/**
 * @author gideon
 */
public class LongFieldPart<O> extends NumberFieldPart<O, Long> implements LongField<O> {

    public LongFieldPart(Table<O> table, String javaName) {
        super(table, LONG, Long.class, javaName);
    }


    @Override
    public Continuation<O> eq(Long value) throws OrmException {
        return new ValueExpressionPart<>(LONG, getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Long value) throws OrmException {
        return new ValueExpressionPart<>(LONG, getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<O> lt(Long value) throws OrmException {
        return new ValueExpressionPart<>(LONG, getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Long value) throws OrmException {
        return new ValueExpressionPart<>(LONG, getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Long value) throws OrmException {
        return new ValueExpressionPart<>(LONG, getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Long value) throws OrmException {
        return new ValueExpressionPart<>(LONG, getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<O> in(List<Long> values) throws OrmException {
        return new ListExpressionPart<>( getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Long> values) throws OrmException {
        return new ListExpressionPart<>( getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Long... values) throws OrmException {
        return new ListExpressionPart<>( getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Long... values) throws OrmException {
        return new ListExpressionPart<>( getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

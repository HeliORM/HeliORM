package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.ShortField;

import java.util.Arrays;
import java.util.List;

import static com.heliorm.Field.FieldType.SHORT;

/**
 * @author gideon
 */
public class ShortFieldPart<O> extends NumberFieldPart<O, Short> implements ShortField<O> {

    public ShortFieldPart(Table<O> table, String javaName) {
        super(table, SHORT, Short.class, javaName);
    }

    @Override
    public Continuation<O> eq(Short value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Short value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<O> lt(Short value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Short value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Short value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Short value) throws OrmException {
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
    public Continuation<O> in(List<Short> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Short> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Short... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Short... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

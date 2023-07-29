package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.DateField;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author gideon
 */
public final class DateFieldPart<O> extends FieldPart<O, Date> implements
        DateField<O>,
        WithRangePart<O, Date>,
        WithEqualsPart<O, Date>,
        WithInPart<O, Date>, WithIsPart<O, Date> {

    public DateFieldPart(Table<O> table, String javaName) {
        super(table, FieldType.DATE, Date.class, javaName);
    }


    @Override
    public Continuation<O> eq(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<O> lt(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<O> in(List<Date> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Date> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Date... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Date... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.DateField;
import com.heliorm.Table;
import com.heliorm.def.Continuation;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gideon
 */
public final class DateFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Date> implements
        DateField<T, O>,
        WithRangePart<T, O, Date>,
        WithEqualsPart<T, O, Date>,
        WithInPart<T, O, Date>, WithIsPart<T, O, Date> {

    public DateFieldPart(T table, String javaName) {
        super(table, FieldType.DATE, Date.class, javaName);
    }


    @Override
    public Continuation<T, O> eq(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<T, O> lt(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(Date value) throws OrmException {
        return new DateValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<T, O> in(List<Date> values) throws OrmException {
        return new DateListExpressionPart((DateFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<Date> values) throws OrmException {
        return new DateListExpressionPart((DateFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(Date... values) throws OrmException {
        return new DateListExpressionPart((DateFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(Date... values) throws OrmException {
        return new DateListExpressionPart((DateFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.LocalDateField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.heliorm.Field.FieldType.LOCAL_DATE;

/**
 * @author gideon
 */
public final class LocalDateFieldPart<O> extends FieldPart<O, LocalDate> implements
        LocalDateField<O>,
        WithRangePart<O, LocalDate>,
        WithEqualsPart<O, LocalDate>,
        WithInPart<O, LocalDate>, WithIsPart<O, LocalDate> {

    public LocalDateFieldPart(Table<O> table, String javaName) {
        super(table, LOCAL_DATE, LocalDate.class, javaName);
    }


    @Override
    public Continuation<O> eq(LocalDate value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(LocalDate value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<O> lt(LocalDate value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(LocalDate value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(LocalDate value) throws OrmException {
        return new ValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(LocalDate value) throws OrmException {
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
    public Continuation<O> in(List<LocalDate> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<LocalDate> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(LocalDate... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(LocalDate... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

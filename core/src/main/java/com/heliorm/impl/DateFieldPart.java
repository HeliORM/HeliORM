package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.DateField;
import com.heliorm.Table;
import com.heliorm.def.ExpressionContinuation;

import java.util.Date;

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
    public ExpressionContinuation<T, O> eq(Date value) {
        return new DateValueExpressionPart(this, ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Date value) {
        return new DateValueExpressionPart(this, ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public ExpressionContinuation<T, O> lt(Date value) throws OrmException {
        return new DateValueExpressionPart(this, ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Date value) throws OrmException {
        return new DateValueExpressionPart(this, ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Date value) throws OrmException {
        return new DateValueExpressionPart(this, ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Date value) throws OrmException {
        return new DateValueExpressionPart(this, ValueExpressionPart.Operator.GE, value);
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

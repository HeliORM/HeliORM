package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.DoubleField;
import com.heliorm.def.ExpressionContinuation;

/**
 *
 * @author gideon
 */
public final class DoubleFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Double> implements DoubleField<T, O> {

    public DoubleFieldPart(T table, String javaName) {
        super(table, FieldType.DOUBLE, Double.class, javaName);
    }

    @Override
    public ExpressionContinuation<T, O> eq(Double value) {
        return new DoubleValueExpressionPart(this, ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Double value) {
        return new DoubleValueExpressionPart(this, ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public ExpressionContinuation<T, O> lt(Double value) throws OrmException {
        return new DoubleValueExpressionPart(this, ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Double value) throws OrmException {
        return new DoubleValueExpressionPart(this, ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Double value) throws OrmException {
        return new DoubleValueExpressionPart(this, ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Double value) throws OrmException {
        return new DoubleValueExpressionPart(this, ValueExpressionPart.Operator.GE, value);
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

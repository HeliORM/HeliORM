package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.FloatField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class FloatFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Float> implements FloatField<T, O> {

    public FloatFieldPart(T table, String javaName) {
        super(table, FieldType.FLOAT, Float.class, javaName);
    }


    @Override
    public ExpressionContinuation<T, O> eq(Float value) throws OrmException {
        return new FloatValueExpressionPart((FloatFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Float value) throws OrmException {
        return new FloatValueExpressionPart((FloatFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> lt(Float value) throws OrmException {
        return new FloatValueExpressionPart((FloatFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Float value) throws OrmException {
        return new FloatValueExpressionPart((FloatFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Float value) throws OrmException {
        return new FloatValueExpressionPart((FloatFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Float value) throws OrmException {
        return new FloatValueExpressionPart((FloatFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
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

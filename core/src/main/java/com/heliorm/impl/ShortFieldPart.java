package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.ShortField;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class ShortFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Short> implements ShortField<T, O> {

    public ShortFieldPart(T table, String javaName) {
        super(table, FieldType.SHORT, Short.class, javaName);
    }

    @Override
    public ExpressionContinuation<T, O> eq(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public ExpressionContinuation<T, O> lt(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Short value) throws OrmException {
        return new ShortValueExpressionPart((ShortFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
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

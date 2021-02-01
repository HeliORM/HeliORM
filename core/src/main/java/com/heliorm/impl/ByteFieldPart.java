package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.ByteField;
import com.heliorm.def.ExpressionContinuation;

/**
 *
 * @author gideon
 */
public final class ByteFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Byte> implements ByteField<T, O> {

    public ByteFieldPart(T table, String javaName) {
        super(table, FieldType.BYTE, Byte.class, javaName);
    }

    @Override
    public ExpressionContinuation<T, O> eq(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> lt(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public ExpressionContinuation<T, O> le(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public ExpressionContinuation<T, O> gt(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public ExpressionContinuation<T, O> ge(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public ExpressionContinuation<T, O> isNull() throws OrmException {
        return new IsExpressionPart((ByteFieldPart) getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public ExpressionContinuation<T, O> isNotNull() throws OrmException {
        return new IsExpressionPart((ByteFieldPart) getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

}

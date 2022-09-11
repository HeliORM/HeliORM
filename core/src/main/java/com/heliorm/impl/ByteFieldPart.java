package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.ByteField;
import com.heliorm.def.Continuation;

import java.util.Arrays;
import java.util.List;

/**
 * @author gideon
 */
public final class ByteFieldPart<O> extends NumberFieldPart<O, Byte> implements ByteField<O> {

    public ByteFieldPart(Table<O> table, String javaName) {
        super(table, FieldType.BYTE, Byte.class, javaName);
    }

    @Override
    public Continuation<O> eq(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<O> lt(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<O> le(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<O> gt(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<O> ge(Byte value) throws OrmException {
        return new ByteValueExpressionPart((ByteFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
    }

    @Override
    public Continuation<O> isNull() throws OrmException {
        return new IsExpressionPart((ByteFieldPart) getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public Continuation<O> isNotNull() throws OrmException {
        return new IsExpressionPart((ByteFieldPart) getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

    @Override
    public Continuation<O> in(List<Byte> values) throws OrmException {
        return new ByteListExpressionPart((ByteFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<Byte> values) throws OrmException {
        return new ByteListExpressionPart((ByteFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<O> in(Byte... values) throws OrmException {
        return new ByteListExpressionPart((ByteFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<O> notIn(Byte... values) throws OrmException {
        return new ByteListExpressionPart((ByteFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

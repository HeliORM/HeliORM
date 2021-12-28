package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.LongField;
import com.heliorm.Table;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gideon
 */
public class LongFieldPart<T extends Table<O>, O> extends NumberFieldPart<T, O, Long> implements LongField<T, O> {

    public LongFieldPart(T table, String javaName) {
        super(table, FieldType.LONG, Long.class, javaName);
    }


    @Override
    public Continuation<T, O> eq(Long value) throws OrmException {
        return new LongValueExpressionPart((LongFieldPart) getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Long value) throws OrmException {
        return new LongValueExpressionPart((LongFieldPart) getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }


    @Override
    public Continuation<T, O> lt(Long value) throws OrmException {
        return new LongValueExpressionPart((LongFieldPart) getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O> le(Long value) throws OrmException {
        return new LongValueExpressionPart((LongFieldPart) getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O> gt(Long value) throws OrmException {
        return new LongValueExpressionPart((LongFieldPart) getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O> ge(Long value) throws OrmException {
        return new LongValueExpressionPart((LongFieldPart) getThis(), ValueExpressionPart.Operator.GE, value);
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
    public Continuation<T, O> in(List<Long> values) throws OrmException {
        return new LongListExpressionPart((LongFieldPart) getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<Long> values) throws OrmException {
        return new LongListExpressionPart((LongFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(Long... values) throws OrmException {
        return new LongListExpressionPart((LongFieldPart) getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(Long... values) throws OrmException {
        return new LongListExpressionPart((LongFieldPart) getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

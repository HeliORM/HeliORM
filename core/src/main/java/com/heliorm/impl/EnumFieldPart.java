package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.EnumField;

import java.util.Arrays;
import java.util.List;

/**
 * @param <O> Type of POJO
 * @param <E> Type of the enum
 * @author gideon
 */
public final class EnumFieldPart<O, E extends Enum<E>> extends FieldPart<O, E> implements
        EnumField<O, E>,
        WithEqualsPart<O, E>,
        WithInPart<O, E>, WithIsPart<O, E> {

    public EnumFieldPart(Table<O> table, Class<E> javaType, String javaName) {
        super(table, FieldType.ENUM, javaType, javaName);
    }

    @Override
    public Continuation<O> eq(E value) throws OrmException {
        return new ValueExpressionPart<>(FieldType.ENUM, getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(E value) throws OrmException {
        return new ValueExpressionPart<>(FieldType.ENUM, getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
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
    public Continuation<O> in(List<E> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<O> notIn(List<E> values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @SafeVarargs
    @Override
    public final Continuation<O> in(E... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @SafeVarargs
    @Override
    public final Continuation<O> notIn(E... values) throws OrmException {
        return new ListExpressionPart<>(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.EnumField;

import java.util.Arrays;
import java.util.List;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <E> Type of the enum
 * @author gideon
 */
public class EnumFieldPart<T extends Table<O>, O, E extends Enum> extends FieldPart<T, O, E> implements
        EnumField<T, O, E>,
        WithEqualsPart<T, O, E>,
        WithInPart<T, O, E>, WithIsPart<T, O, E> {

    public EnumFieldPart(T table, Class<E> javaType, String javaName) {
        super(table, FieldType.ENUM, javaType, javaName);
    }

    @Override
    public Continuation<T, O> eq(E value) throws OrmException {
        return new EnumValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(E value) throws OrmException {
        return new EnumValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
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
    public Continuation<T, O> in(List<E> values) throws OrmException {
        return new EnumListExpressionPart(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O> notIn(List<E> values) throws OrmException {
        return new EnumListExpressionPart(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O> in(E... values) throws OrmException {
        return new EnumListExpressionPart(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O> notIn(E... values) throws OrmException {
        return new EnumListExpressionPart(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

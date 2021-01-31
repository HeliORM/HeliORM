package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.EnumField;
import com.heliorm.def.ExpressionContinuation;

/**
 * @author gideon
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <E> Type of the enum
 *
 */
public class EnumFieldPart<T extends Table<O>, O, E extends Enum> extends FieldPart<T, O, E> implements
        EnumField<T, O, E>,
        WithEqualsPart<T, O, E>,
        WithInPart<T, O, E>, WithIsPart<T, O, E> {

    public EnumFieldPart(T table, Class<E> javaType, String javaName) {
        super(table, FieldType.ENUM, javaType, javaName);
    }

    @Override
    public ExpressionContinuation<T, O> eq(E value) {
        return new EnumValueExpressionPart(this, ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public ExpressionContinuation<T, O> notEq(E value) {
        return new EnumValueExpressionPart(this, ValueExpressionPart.Operator.NOT_EQ, value);
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

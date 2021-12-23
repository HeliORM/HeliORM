package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.BooleanField;
import com.heliorm.def.Continuation;

/**
 * @author gideon
 */
public class
BooleanFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Boolean> implements
        BooleanField<T, O>,
        WithEqualsPart<T, O, Boolean>, WithIsPart<T, O, Boolean> {

    public BooleanFieldPart(T table, String javaName) {
        super(table, FieldType.BOOLEAN, Boolean.class, javaName);
    }

    @Override
    public Continuation<T, O> eq(Boolean value) throws OrmException {
        return new BooleanValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O> notEq(Boolean value) throws OrmException {
        return new BooleanValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O> isNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public Continuation<T, O> isNotNull() throws OrmException {
        return new IsExpressionPart(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

}

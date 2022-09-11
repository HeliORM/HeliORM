package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.BooleanField;
import com.heliorm.def.Continuation;

/**
 * @author gideon
 */
public class
BooleanFieldPart<O> extends FieldPart<O, Boolean> implements
        BooleanField<O>,
        WithEqualsPart<O, Boolean>, WithIsPart<O, Boolean> {

    public BooleanFieldPart(Table<O> table, String javaName) {
        super(table, FieldType.BOOLEAN, Boolean.class, javaName);
    }

    @Override
    public Continuation<O> eq(Boolean value) throws OrmException {
        return new BooleanValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public Continuation<O> notEq(Boolean value) throws OrmException {
        return new BooleanValueExpressionPart<>(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<O> isNull() throws OrmException {
        return new IsExpressionPart<>(getThis(), IsExpressionPart.Operator.IS_NULL);
    }

    @Override
    public Continuation<O> isNotNull() throws OrmException {
        return new IsExpressionPart<>(getThis(), IsExpressionPart.Operator.IS_NOT_NULL);
    }

}

package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.ByteArrayField;

import static com.heliorm.Field.FieldType.BYTE_ARRAY;

/**
 * @author gideon
 */
public final class ByteArrayFieldPart<O> extends FieldPart<O, byte[]> implements
        ByteArrayField<O> {

    public ByteArrayFieldPart(Table<O> table, String javaName) {
        super(table, BYTE_ARRAY, byte[].class, javaName);
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

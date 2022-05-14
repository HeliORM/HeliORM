package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

public final class ByteValueExpressionPart<T extends Table<O>, O> extends NumberValueExpressionPart<T, O, Byte> {

    private Byte value;

    public ByteValueExpressionPart(NumberFieldPart left, Operator op, Byte value) {
        super(Field.FieldType.BYTE, left, op);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

}


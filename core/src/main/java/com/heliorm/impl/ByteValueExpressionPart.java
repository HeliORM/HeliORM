package com.heliorm.impl;

import com.heliorm.Field;

public final class ByteValueExpressionPart< O> extends NumberValueExpressionPart<O, Byte> {

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


package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

import static java.lang.String.format;

public final class ByteValueExpressionPart<T extends Table<O>, O> extends NumberValueExpressionPart<T,O, Byte> {

    private Byte value;

    public ByteValueExpressionPart(NumberFieldPart left, Operator op, Byte value) {
        super(Field.FieldType.BYTE, left, op);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return format("%s %s", getOperator(), getValue());
    }
}


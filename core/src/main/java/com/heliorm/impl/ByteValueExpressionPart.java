package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

public final class ByteValueExpressionPart<T extends Table<O>, O> extends ValueExpressionPart<T,O, Byte> {

    private Byte value;

    public ByteValueExpressionPart(FieldPart left, Operator op, Byte value) {
        super(Field.FieldType.BYTE, left, op);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }




}


package com.heliorm.impl;

import com.heliorm.Field;

public final class EnumValueExpressionPart<O, E extends Enum> extends ValueExpressionPart<O, E> {

    private final E value;

    public EnumValueExpressionPart(FieldPart<O,E> left, Operator op, E value) {
        super(Field.FieldType.ENUM, left, op);
        this.value = value;
    }

    @Override
    public E getValue() {
        return value;
    }

}


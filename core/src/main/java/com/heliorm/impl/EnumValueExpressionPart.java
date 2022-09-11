package com.heliorm.impl;

import com.heliorm.Field;

public final class EnumValueExpressionPart<O, E extends Enum> extends ValueExpressionPart<O, E> {

    private final Enum value;
    private final Class<E> enumType;

    public EnumValueExpressionPart(FieldPart left, Operator op, E value) {
        super(Field.FieldType.ENUM, left, op);
        this.value = value;
        this.enumType = (Class<E>) value.getClass();
    }

    @Override
    public E getValue() {
        return (E) value;
    }

}


package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

public final class EnumValueExpressionPart<T extends Table<O>, O, E extends Enum> extends ValueExpressionPart<T, O, E> {

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


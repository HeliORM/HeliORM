package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

public final class EnumValueExpressionPart<T extends Table<O>, O, E extends Enum> extends ValueExpressionPart<T,O, E> {

    private Enum value;

    public EnumValueExpressionPart(FieldPart left, Operator op, E value) {
        super(Field.FieldType.ENUM, left, op);
        this.value = value;
    }

    @Override
    public E getValue() {
        return (E) value;
    }

   


}


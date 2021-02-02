package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ByteListExpressionPart<T extends Table<O>,O> extends NumberListExpressionPart<T,O,Byte>  {

    private final List<Byte> values;

    public ByteListExpressionPart(ByteFieldPart left, Operator op, Collection<Byte> values) {
        super(Field.FieldType.BYTE, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List getValues() {
        return values;
    }
}

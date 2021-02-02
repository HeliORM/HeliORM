package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FloatListExpressionPart<T extends Table<O>,O> extends NumberListExpressionPart<T,O,Float>  {

    private final List<Float> values;

    public FloatListExpressionPart(FloatFieldPart left, Operator op, Collection<Byte> values) {
        super(Field.FieldType.FLOAT, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List getValues() {
        return values;
    }
}

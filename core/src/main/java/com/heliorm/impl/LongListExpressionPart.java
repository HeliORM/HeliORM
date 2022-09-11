package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class LongListExpressionPart<T extends Table<O>, O> extends NumberListExpressionPart<O, Long> {

    private final List<Long> values;

    public LongListExpressionPart(LongFieldPart left, Operator op, Collection<Byte> values) {
        super(Field.FieldType.LONG, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List getValues() {
        return values;
    }
}

package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class EnumListExpressionPart<T extends Table<O>, O, E extends Enum> extends ListExpressionPart<T, O, E> {

    private final List<Enum> values;

    public EnumListExpressionPart(FieldPart left, Operator op, Collection<Duration> values) {
        super(Field.FieldType.ENUM, left, op);
        this.values = new ArrayList(values);
    }

    @Override
    public List<E> getValues() {
        return (List<E>) values;
    }
}

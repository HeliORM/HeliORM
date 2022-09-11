package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Index;

import java.util.List;

public final class IndexPart<O> implements Index<O> {

    private final boolean unique;
    private final List<Field<O, ?>> fields;

    public IndexPart(boolean unique, List<Field<O, ?>> fields) {
        this.unique = unique;
        this.fields = fields;
    }

    @Override
    public List<Field<O, ?>> getFields() {
        return fields;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }
}

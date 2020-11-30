package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.def.Index;

import java.util.List;

public final class IndexPart<T extends Table<O>, O> implements Index<T,O> {

    private final boolean unique;
    private final List<Field<T,O,?>> fields;

    public IndexPart(boolean unique, List<Field<T, O, ?>> fields) {
        this.unique = unique;
        this.fields = fields;
    }

    @Override
    public List<Field<T, O, ?>> getFields() {
        return fields;
    }
    @Override
    public boolean isUnique() {
        return unique;
    }
}

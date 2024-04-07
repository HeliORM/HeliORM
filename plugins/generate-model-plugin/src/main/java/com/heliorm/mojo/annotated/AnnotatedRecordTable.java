package com.heliorm.mojo.annotated;

import com.heliorm.Database;
import com.heliorm.Field;
import com.heliorm.Index;
import com.heliorm.Table;
import com.heliorm.annotation.Pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class AnnotatedRecordTable<O> implements Table<O> {

    private final Database database;
    private final Class<O> type;

    public AnnotatedRecordTable(Database database, Class<O> type) {
        this.type = type;
        this.database = database;
    }

    @Override
    public Class<O> getObjectClass() {
        return type;
    }

    @Override
    public List<Field<O, ?>> getFields() {
        return new ArrayList<>(Arrays.stream(type.getRecordComponents())
                .map(com -> new AnnotatedRecordField<>(this, com))
                .toList());
    }

    @Override
    public Optional<Field<O, ?>> getPrimaryKey() {
        return getFields().stream()
                .filter(Field::isPrimaryKey)
                .findFirst();
    }

    @Override
    public String getSqlTable() {
        var pojo = type.getAnnotation(Pojo.class);
        if (pojo != null) {
            if (!pojo.tableName().isEmpty()) {
                return pojo.tableName();
            }
        }
        return type.getSimpleName();
    }

    @Override
    public Set<Table<?>> getSubTables() {
        return Set.of();
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isRecord() {
        return true;
    }

    @Override
    public List<Index<O>> getIndexes() {
        return new ArrayList<>(Arrays.stream(type.getAnnotations())
                .filter(ann -> ann.annotationType().equals(com.heliorm.annotation.Index.class))
                .map(ann -> new AnnotatedRecordIndex<>(this, (com.heliorm.annotation.Index) ann))
                .toList());
    }
}

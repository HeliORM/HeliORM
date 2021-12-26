package com.heliorm.sql.structure;

import com.heliorm.Field;
import com.heliorm.sql.Column;
import com.heliorm.sql.Database;
import com.heliorm.sql.Index;
import com.heliorm.sql.Table;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StructureTable implements Table {

    private final com.heliorm.Table<?> table;
    private final Set<Column> columns;
    private final Set<Index> indexes;

    public StructureTable(com.heliorm.Table<?> table) {
        this.table = table;
        columns = table.getFields().stream().map(field -> makeColumn(field)).collect(Collectors.toSet());
        indexes = table.getIndexes().stream()
                .map(index -> new StructureIndex(this, index)).collect(Collectors.toSet());
    }

    @Override
    public Database getDatabase() {
        return new StructureDatabase(table.getDatabase());
    }

    @Override
    public String getName() {
        return table.getSqlTable();
    }

    @Override
    public Set<Column> getColumns() {
        return columns;
    }

    @Override
    public Column getColumn(String s) {
        return columns.stream()
                .filter(column -> column.getName().equals(s)).findFirst().get();
    }

    @Override
    public Set<Index> getIndexes() {
        return indexes;
    }

    @Override
    public Index getIndex(String s) {
        return null;
    }

    private Column makeColumn(Field field) {
        switch (field.getFieldType()) {
            case ENUM:
                return new StructureEnumColumn(this,field, getEnumValues(field));
            default:
                return new StructureColumn(this, field);
        }
    }

    private Set<String> getEnumValues(Field<?, ?, ?> field) {
        Class<?> javaType = field.getJavaType();
        return Arrays.stream(javaType.getEnumConstants())
                .map(v -> ((Enum)v).name())
                .collect(Collectors.toSet());
    }
}

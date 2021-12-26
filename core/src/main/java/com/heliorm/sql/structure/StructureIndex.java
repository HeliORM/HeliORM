package com.heliorm.sql.structure;

import com.heliorm.sql.Column;
import com.heliorm.sql.Index;
import com.heliorm.sql.Table;

import java.util.Set;
import java.util.stream.Collectors;

public class StructureIndex implements Index {

    private final StructureTable table;
    private final  com.heliorm.Index<?,?> index;
    private final Set<Column> columns;

    StructureIndex(StructureTable table, com.heliorm.Index<?,?> index) {
        this.table =table;
        this.index = index;
        this.columns = index.getFields().stream()
                .map(field-> field.getSqlName())
                .map(name -> table.getColumn(name))
                .collect(Collectors.toSet());
    }


    @Override
    public String getName() {
        return index.getFields().stream()
                .map(field -> field.getSqlName())
                .reduce("idx", (a,b) -> a + "_" + b);
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public Set<Column> getColumns() {
        return columns;
    }

    @Override
    public boolean isUnique() {
        return index.isUnique();
    }
}

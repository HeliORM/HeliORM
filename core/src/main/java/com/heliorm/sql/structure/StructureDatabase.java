package com.heliorm.sql.structure;

import com.heliorm.sql.Database;
import com.heliorm.sql.Table;

import java.util.Set;
import java.util.stream.Collectors;

public class StructureDatabase implements Database {

    private final com.heliorm.Database database;

    public StructureDatabase(com.heliorm.Database database) {
        this.database = database;
    }

    @Override
    public String getName() {
        return database.getSqlDatabase();
    }

    @Override
    public Set<Table> getTables() {
        return database.getTables().stream()
                .map(table -> new StructureTable(table)).collect(Collectors.toSet());
    }
}

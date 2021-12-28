package com.heliorm.impl;

import com.heliorm.Database;
import com.heliorm.Table;
import com.heliorm.Field;
import com.heliorm.Index;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author gideon
 */
public class AliasTable<O> implements Table<O> {

    private final Database database;
    private final Table<O> table;
    private Set<Table<?>> subs;

    public AliasTable(Database database, Table table) {
        this.database = database;
        this.table = table;
    }

    @Override
    public Class<O> getObjectClass() {
        return table.getObjectClass();
    }

    @Override
    public List<Field> getFields() {
        return table.getFields();
    }

    @Override
    public Optional<Field> getPrimaryKey() {
        return table.getPrimaryKey();
    }

    @Override
    public String getSqlTable() {
        return table.getSqlTable();
    }

    @Override
    public Set<Table<?>> getSubTables() {
        if (subs == null) {
            subs = new HashSet();
            for (Table<?> sub : table.getSubTables()) {
                subs.add(new AliasTable(database, sub));
            }
        }
        return subs;
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public boolean isAbstract() {
      return table.isAbstract();
    }

    @Override
    public List<Index> getIndexes() {
        return table.getIndexes();
    }
}

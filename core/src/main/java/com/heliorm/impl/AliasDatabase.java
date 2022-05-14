package com.heliorm.impl;

import com.heliorm.Database;
import com.heliorm.Table;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gideon
 */
public class AliasDatabase implements Database {

    private final Database database;
    private final String sqlDatabase;
    private List<Table<?>> tables;

    /** Create a new alias database.
     *
     * @param database The underlying database model
     * @param sqlDatabase The name of the SQL database
     */
    public AliasDatabase(Database database, String sqlDatabase) {
        this.database = database;
        this.sqlDatabase = sqlDatabase;
    }

    @Override
    public String getSqlDatabase() {
        return sqlDatabase;
    }

    @Override
    public List<Table<?>> getTables() {
        if (tables == null) {
            tables = new ArrayList();
            for (Table<?> table : database.getTables()) {
                tables.add(new AliasTable<>(this, table));
            }
        }
        return tables;
    }

}

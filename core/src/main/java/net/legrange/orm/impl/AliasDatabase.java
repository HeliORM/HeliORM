package net.legrange.orm.impl;

import java.util.ArrayList;
import java.util.List;
import net.legrange.orm.Database;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class AliasDatabase implements Database {

    private final Database database;
    private final String sqlDatabase;
    private List<Table> tables;

    public AliasDatabase(Database database, String sqlDatabase) {
        this.database = database;
        this.sqlDatabase = sqlDatabase;
    }

    @Override
    public String getSqlDatabase() {
        return sqlDatabase;
    }

    @Override
    public List<Table> getTables() {
        if (tables == null) {
            tables = new ArrayList();
            for (Table<?> table : database.getTables()) {
                tables.add(new AliasTable<>(this, table));
            }
        }
        return tables;
    }

}

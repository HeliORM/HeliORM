package net.legrange.orm.mojo;

import java.util.ArrayList;
import java.util.List;
import net.legrange.orm.Database;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class PackageDatabase implements Database {

    private final String packageName;
    private String sqlDatabase;
    private final List<Table> tables = new ArrayList();

    public PackageDatabase(String packageName) {
        this.packageName = packageName;
        sqlDatabase = "";
    }

    @Override
    public String getSqlDatabase() {
        return sqlDatabase;
    }

    @Override
    public List<Table> getTables() {
        return tables;
    }

    public String getPackageName() {
        return packageName;
    }

    void addTable(Table table) {
        tables.add(table);
    }

    void setSqlDatabase(String sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
    }

}

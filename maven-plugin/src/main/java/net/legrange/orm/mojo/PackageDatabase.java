package net.legrange.orm.mojo;

import java.util.HashSet;
import java.util.Set;
import net.legrange.orm.Database;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class PackageDatabase implements Database {

    private final String packageName;
    private final Set<Table> tables = new HashSet();

    public PackageDatabase(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getSqlDatabase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Table> getTables() {
        return tables;
    }

    void addTable(Table table) {
        tables.add(table);
    }

}

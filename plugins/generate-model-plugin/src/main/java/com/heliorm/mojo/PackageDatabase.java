package com.heliorm.mojo;

import com.heliorm.Database;
import com.heliorm.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of database that organizes tables for POJO classes in a table
 * with the longest package name that includes all the POJO packages.
 *
 * @author gideon
 */
public class PackageDatabase implements Database {

    private final String packageName;
    private final List<Table<?>> tables = new ArrayList<>();
    private String sqlDatabase;

    /**
     * Create a database package with the given package name.
     *
     */
    public PackageDatabase(String packageName) {
        this.packageName = packageName;
        sqlDatabase = "";
    }

    @Override
    public String getSqlDatabase() {
        return sqlDatabase;
    }

    /**
     * Set the SQL database name.
     *
     * @param sqlDatabase The SQL database name
     */
    void setSqlDatabase(String sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
    }

    @Override
    public List<Table<?>> getTables() {
        return tables;
    }

    /**
     * Return the package name for the database package.
     *
     * @return The package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Add a table to this database.
     *
     * @param table The table to add
     */
    void addTable(Table<?> table) {
        tables.add(table);
    }

}

package com.heliorm.sql;

import com.heliorm.Database;
import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static java.lang.String.format;

/**
 * Abstract implementation of a database specific driver
 *
 * @author gideon
 */
public abstract class SqlDriver {

    private final Map<Database, Database> aliases;
    private boolean rollbackOnUncommittedClose = false;
    private boolean useUnionAll = false;
    private boolean createTables = false;

    public SqlDriver(Map<Database, Database> aliases) {
        this.aliases = aliases;
    }

    public final void setUseUnionAll(boolean useUnionAll) {
        this.useUnionAll = useUnionAll;
    }

    public boolean createTables() {
        return createTables;
    }

    /**
     * Configure driver to create missing SQL tables.
     *
     * @param createTables True to create tables
     */
    public final void setCreateTables(boolean createTables) {
        this.createTables = createTables;
    }

    final boolean getRollbackOnUncommittedClose() {
        return rollbackOnUncommittedClose;
    }

    /**
     * Set the driver to rollback or commit data on an un-committed auto-close
     * s
     *
     */
    public final void setRollbackOnUncommittedClose(boolean rollback) {
        rollbackOnUncommittedClose = rollback;
    }

    final boolean useUnionAll() {
        return useUnionAll && supportsUnionAll();
    }

    protected void setEnum(PreparedStatement stmt, int par, String value) throws SQLException {
        stmt.setString(par, value);
    }

    protected abstract boolean supportsUnionAll();

    protected abstract boolean supportsTransactions();

    protected abstract String castNull(Field<?,?> field) throws OrmException;

    /**
     * Retrieve the returned key value from a result set (used for updating
     * auto-increment keys).
     *
     * @param rs    The result set
     * @param field The field for which we're reading data
     * @return The data
     * @throws OrmException Thrown if we cannot work out how to extract the
     *                      data.
     */
    protected abstract Object getKeyValueFromResultSet(ResultSet rs, Field<?,?> field) throws OrmException;


    /**
     * Work out the exact table name to use.
     *
     * @param table The table we're referencing
     * @return The SQL table name
     * @throws OrmException Thrown if something goes wrong determining the table name
     */
    protected abstract String fullTableName(Table<?> table) throws OrmException;

    /**
     * Work out the exact field name to use.
     *
     * @param field The field
     * @param table The table
     * @return The SQL field name
     * @throws OrmException Thrown if something goes wrong determining the field name
     */
    protected abstract String fullFieldName(Table<?> table, Field<?,?> field) throws OrmException;

    protected abstract String fieldType(Table<?> table, Field<?,?> field) throws OrmException;

    /**
     * Work out the short field name to use.
     *
     * @param field The field
     * @param table The table
     * @return The SQL field name
     * @throws OrmException Thrown if something goes wrong determining the field name
     */
    protected abstract String fieldName(Table<?> table, Field<?,?> field) throws OrmException;

    /**
     * Create a virtual field name based on the supplied value
     *
     * @param name The name
     * @return The correctly quoted field name
     */
    protected abstract String virtualFieldName(String name);

    /**
     * Create a virtual field value based on the supplied value
     *
     * @param name The name
     * @return The correctly quoted field name
     */
    protected abstract String virtualValue(String name);


    /**
     * Get the table generator for this driver
     *
     * @return The table generator
     * @throws OrmException Thrown if there isn't one.
     */
    protected abstract TableGenerator getTableGenerator() throws OrmException;

    /**
     * Work out the short table name to use.
     *
     * @param table The table we're referencing
     * @return The SQL table name
     */
    protected final String tableName(Table table) {
        return makeTableName(table);
    }

    final String makeTableName(Table table) {
        return format("%s", table.getSqlTable());
    }

    /**
     * Work out the database name to use.
     *
     * @param table The table we're referencing
     * @return The SQL table name
     */
    protected final String databaseName(Table table) {
        Database database = table.getDatabase();
        Database alias = aliases.get(database);
        if (alias == null) {
            alias = database;
        }
        return format("%s", alias.getSqlDatabase());
    }

}

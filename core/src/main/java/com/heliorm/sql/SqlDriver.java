package com.heliorm.sql;

import com.heliorm.Database;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * @author gideon
 */
public abstract class SqlDriver {

    private boolean rollbackOnUncommittedClose = false;
    private boolean useUnionAll = false;
    private boolean createTables;
    private final Map<Database, Database> aliases;
    private final Map<Field, String> fieldIds = new ConcurrentHashMap<>();

    public SqlDriver(Map<Database, Database> aliases) {
        this.aliases = aliases;
    }

    public final void setRollbackOnUncommittedClose(boolean rollback) {
        rollbackOnUncommittedClose = rollback;
    }

    public final void setUseUnionAll(boolean useUnionAll) {
        this.useUnionAll = useUnionAll;
    }

    public boolean isCreateTables() {
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

    final boolean useUnionAll() {
        return useUnionAll && supportsUnionAll();
    }

    protected void setEnum(PreparedStatement stmt, int par, String value) throws SQLException {
        stmt.setString(par, value);
    }

    protected abstract boolean supportsUnionAll();

    protected abstract boolean supportsTransactions();

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
    protected abstract Object getKeyValueFromResultSet(ResultSet rs, Field field) throws OrmException;


    /**
     * Work out the exact table name to use.
     *
     * @param table The table we're referencing
     * @return The SQL table name
     * @throws OrmException Thrown if something goes wrong determining the table name
     */
    protected abstract String fullTableName(Table table) throws OrmException;

    /**
     * Work out the exact field name to use.
     *
     * @param field The field
     * @param table The table
     * @return The SQL field name
     * @throws OrmException Thrown if something goes wrong determining the field name
     */
    protected abstract String fullFieldName(Table table, Field field) throws OrmException;

    /**
     * Work out the short field name to use.
     *
     * @param field The field
     * @param table The table
     * @return The SQL field name
     * @throws OrmException Thrown if something goes wrong determining the field name
     */
    protected abstract String fieldName(Table table, Field field) throws OrmException;

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
    protected final String tableName(Table table) throws OrmException {
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



    String getFieldId(Field field) {
        return fieldIds.computeIfAbsent(field, k -> makeFieldId(k));
    }

    private String makeFieldId(Field field) {
        String uuid;
        do { // very simple collison avoidance
            uuid = UUID.randomUUID().toString().substring(0, 8);
        }
        while (fieldIds.containsKey(uuid));
        return uuid;
    }


}

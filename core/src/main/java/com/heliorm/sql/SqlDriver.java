package com.heliorm.sql;

import com.heliorm.*;
import com.heliorm.def.Field;
import com.heliorm.query.Order;
import com.heliorm.query.TableSpec;

import java.sql.*;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * @author gideon
 */
public abstract class SqlDriver implements OrmTransactionDriver {

    private final Supplier<Connection> connectionSupplier;
    private SqlTransaction currentTransaction;
    private boolean createTables = false;
    private boolean rollbackOnUncommittedClose = false;
    private boolean useUnionAll = false;
    private final Map<Table, Boolean> exists = new ConcurrentHashMap();
    private final PojoOperations pops;
    private final Map<Database, Database> aliases;
    private final Map<Field, String> fieldIds = new ConcurrentHashMap<>();
    private final ResultSetHelper resultSetHelper;
    private final PojoHelper pojoHelper;
    private final PreparedStatementHelper preparedStatementHelper;

    public SqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops) {
        this(connectionSupplier, pops, Collections.EMPTY_MAP);
    }

    public SqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops, Map<Database, Database> aliases) {
        this.connectionSupplier = connectionSupplier;
        this.pops = pops;
        this.aliases = aliases;
        this.resultSetHelper = new ResultSetHelper(pops, this::getFieldId);
        this.pojoHelper = new PojoHelper(pops);
        this.preparedStatementHelper = new PreparedStatementHelper(pojoHelper, this::setEnum);
    }


    @Override
    public OrmTransaction openTransaction() throws OrmException {
        if (currentTransaction != null) {
            if (currentTransaction.isOpen()) {
                throw new OrmTransactionException(format("A transaction is already open"));
            }
        }
        currentTransaction = new SqlTransaction(this);
        return currentTransaction;
    }

    @Override
    public final void setRollbackOnUncommittedClose(boolean rollback) {
        rollbackOnUncommittedClose = rollback;
    }

    public final void setUseUnionAll(boolean useUnionAll) {
        this.useUnionAll = useUnionAll;
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



    /**
     * Determine if the SQL table exists for a table structure
     *
     * @param table The Table
     * @return True if it exsits in the database
     * @throws OrmException
     */
    private final boolean tableExists(Table table) throws OrmException {
        Connection con = getConnection();
        try {
            DatabaseMetaData dbm = con.getMetaData();
            try (ResultSet tables = dbm.getTables(databaseName(table), null, makeTableName(table), null)) {
                return tables.next();
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(format("Error checking table existance (%s)", ex.getMessage()), ex);
        } finally {
            close(con);
        }
    }


    protected void setEnum(PreparedStatement stmt, int par, String value) throws SQLException {
        stmt.setString(par, value);
    }

    protected abstract boolean supportsUnionAll();

    /**
     * Expand the given order part into the fields of a SQL order clause.
     *
     * @param table The table spec to which the ordering applies
     * @param order The order part
     * @return The partial SQL query string
     */
    private String expandOrder(TableSpec table, Order order) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s", fieldName(table.getTable(), order.getField())));
        if (order.getDirection() == Order.Direction.DESCENDING) {
            query.append(" DESC");
        }
        if (order.getThenBy().isPresent()) {
            query.append(", ");
            query.append(expandOrder(table, order.getThenBy().get()));
        }
        return query.toString();
    }

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
     * Obtain the SQL connection to use
     *
     * @return The connection
     */
    Connection getConnection() {
        if (currentTransaction != null) {
            if (currentTransaction.isOpen()) {
                return currentTransaction.getConnection();
            }
            currentTransaction = null;
        }
        return connectionSupplier.get();
    }



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
        checkTable(table);
        return makeTableName(table);
    }

    private final String makeTableName(Table table) throws OrmException {
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

    private void checkTable(Table table) throws OrmException {
        if (createTables) {
            if (!exists.containsKey(table)) {
                if (!tableExists(table)) {
                    Connection con = getConnection();
                    try (Statement stmt = con.createStatement()) {
                        stmt.executeUpdate(getTableGenerator().generateSchema(table));
                    } catch (SQLException ex) {
                        throw new OrmSqlException(format("Error creating table (%s)", ex.getMessage()), ex);
                    } finally {
                        close(con);
                    }
                }
                exists.put(table, Boolean.TRUE);
            }
        }
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


    /** Close a SQL Connection in a way that properly deals with transactions.
     *
     * @param con
     * @throws OrmException
     */
    private void close(Connection con) throws OrmException {
        if ((currentTransaction == null) || (currentTransaction.getConnection() != con)) {
            try {
                con.close();
            } catch (SQLException ex) {
                throw new OrmException(ex.getMessage(), ex);
            }
        }
    }



}

package com.heliorm.sql;

import com.heliorm.Database;
import com.heliorm.Field;
import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;
import com.heliorm.OrmTransactionException;
import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Join;
import com.heliorm.def.Select;
import com.heliorm.def.Where;
import com.heliorm.impl.ExecutablePart;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.SelectPart;
import com.heliorm.impl.Selector;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

/**
 * A SQL implementation of the ORM.
 *
 * @author gideon
 */
public final class SqlOrm implements Orm {

    private final SqlDriver driver;
    private final Supplier<Connection> connectionSupplier;
    private final PojoOperations pops;

    private final Selector selector;
    private final Map<Class<?>, Table<?>> tables = new ConcurrentHashMap<>();
    private final Map<Table<?>, String> inserts = new ConcurrentHashMap<>();
    private final Map<Table<?>, String> updates = new ConcurrentHashMap<>();
    private final Map<Table<?>, String> deletes = new ConcurrentHashMap<>();
    private final Map<Table<?>, Boolean> exists = new ConcurrentHashMap<>();
    private final QueryHelper queryHelper;
    private final AbstractionHelper abstractionHelper;
    private final PojoHelper pojoHelper;
    private final PreparedStatementHelper preparedStatementHelper;
    private final ResultSetHelper resultSetHelper;
    private SqlTransaction currentTransaction;


    /**
     * Create an ORM mapper using the supplied driver instance. This is meant to
     * be used with third party drivers.
     *
     * @param driver The driver used to access data.
     */
    SqlOrm(SqlDriver driver, Supplier<Connection> connectionSupplier, PojoOperations pops) {
        this.driver = driver;
        this.connectionSupplier = connectionSupplier;
        this.pops = pops;
        this.queryHelper = new QueryHelper(driver, this::getUniqueFieldName, this::fullTableName);
        this.pojoHelper = new PojoHelper(pops);
        this.abstractionHelper = new AbstractionHelper();
        this.preparedStatementHelper = new PreparedStatementHelper(pojoHelper, driver::setEnum);
        this.resultSetHelper = new ResultSetHelper(pops, this::getUniqueFieldName);
        selector = new Selector() {
            @Override
            public <O> List<O> list(Select<O> tail) throws OrmException {
                return SqlOrm.this.list((SelectPart<O>) tail);
            }

            @Override
            public <O> Stream<O> stream(Select<O> tail) throws OrmException {
                return SqlOrm.this.stream((SelectPart<O>) tail);
            }

            @Override
            public <O> Optional<O> optional(Select<O> tail) throws OrmException {
                return SqlOrm.this.optional((SelectPart<O>) tail);
            }

            @Override
            public <O> O one(Select<O> tail) throws OrmException {
                return SqlOrm.this.one((SelectPart<O>) tail);
            }
        };
    }

    @Override
    public <O> O create(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to create a null POJO");
        }
        Table<O> table = tableFor(pojo);
        String query = inserts.get(table);
        if (query == null) {
            query = queryHelper.buildInsertQuery(table);
            inserts.put(table, query);
        }
        Connection con = getConnection();
        O newPojo = pops.newPojoInstance(table);
        for (Field<?,?> field : table.getFields()) {
            pops.setValue(newPojo, field, pops.getValue(pojo, field));
        }
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int par = 1;
            for (Field<?,?> field : table.getFields()) {
                if (field.isPrimaryKey()) {
                    if (field.isAutoNumber()) {
                        if (field.getFieldType() == Field.FieldType.STRING) {
                            if (pops.getValue(newPojo, field) == null) {
                                pops.setValue(newPojo, field, UUID.randomUUID().toString());
                            }
                            preparedStatementHelper.setValueInStatement(stmt, newPojo, field, par);
                            par++;
                        }
                    } else {
                        preparedStatementHelper.setValueInStatement(stmt, newPojo, field, par);
                        par++;
                    }
                } else {
                    preparedStatementHelper.setValueInStatement(stmt, newPojo, field, par);
                    par++;
                }
            }
            stmt.executeUpdate();
            Optional<Field<O, ?>> opt = table.getPrimaryKey();
            if (opt.isPresent()) {
                Field<?,?> keyField = opt.get();
                if (keyField.isAutoNumber()) {
                    if (keyField.getFieldType() != Field.FieldType.STRING) {
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                pops.setValue(newPojo, keyField, driver.getKeyValueFromResultSet(rs, opt.get()));
                            }
                        }
                    }
                }
            }
            return newPojo;
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        } finally {
            closeConnection(con);
        }
    }

    @Override
    public <O> O update(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to update a null POJO");
        }
        Table<O> table = tableFor(pojo);
        String query = updates.get(table);
        if (query == null) {
            query = queryHelper.buildUpdateQuery(table);
            updates.put(table, query);
        }
        Connection con = getConnection();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            int par = 1;
            for (Field<?,?> field : table.getFields()) {
                if (!field.isPrimaryKey()) {
                    preparedStatementHelper.setValueInStatement(stmt, pojo, field, par);
                    par++;
                }
            }
            Optional<Field<O, ?>> primaryKey = table.getPrimaryKey();
            if (primaryKey.isPresent()) {
                Field<O, ?> keyField = primaryKey.get();
                Object val = pojoHelper.getValueFromPojo(pojo, keyField);
                if (val == null) {
                    throw new OrmException(format("No value for key %s for %s in update", keyField.getJavaName(), table.getObjectClass().getSimpleName()));
                }
                preparedStatementHelper.setValueInStatement(stmt, pojo, keyField, par);
                int modified = stmt.executeUpdate();
                if (modified == 0) {
                    throw new OrmException(format("The update did not modify any data for %s with key field/value %s/%s. (Row does not exist)",
                            table.getObjectClass().getSimpleName(),
                            keyField.getJavaName(),
                            val));
                }
                return pojo;
            } else {
                throw new OrmException(format("No primary key for %s in update", table.getObjectClass().getSimpleName()));
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        } finally {
            closeConnection(con);
        }

    }

    @Override
    public <O> void delete(O pojo) throws OrmException {
        if (pojo == null) {
            throw new OrmException("Attempt to delete a null POJO");
        }
        Table<O> table = tableFor(pojo);
        String query = deletes.get(table);
        if (query == null) {
            query = queryHelper.buildDeleteQuery(table);
            deletes.put(table, query);
        }
        Connection con = getConnection();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            Optional<Field<O, ?>> primaryKey = table.getPrimaryKey();
            if (primaryKey.isPresent()) {
                preparedStatementHelper.setValueInStatement(stmt, pojo, primaryKey.get(), 1);
            }
            else {
                throw new OrmException(format("No primary key for %s in delete", table.getObjectClass().getSimpleName()));
            }
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        } finally {
            closeConnection(con);
        }
    }

    @Override
    public <O> Select<O> select(Table<O> table) {
        return new SelectPart<>(selector(), table);
    }

    @Override
    public <O> Select<O> select(Table<O> table, Where<O> where) {
        return new SelectPart<>(selector(), table, where, Collections.emptyList());
    }

    @SafeVarargs
    @Override
    public final <O> Select<O> select(Table<O> table, Join<O>... joins) {
        List<JoinPart<?, ?>> list = Arrays.stream(joins)
                .map(join -> (JoinPart<?, ?>) join).collect(Collectors.toList());
        return new SelectPart<>(selector(), table, null, list);
    }

    @SafeVarargs
    @Override
    public final <O> Select<O> select(Table<O> table, Where<O> where, Join<O>... joins) {
        List<JoinPart<?, ?>> list = Arrays.stream(joins)
                .map(join -> (JoinPart<?, ?>) join).collect(Collectors.toList());
        return new SelectPart<>(selector(), table, where, list);
    }

    @Override
    public OrmTransaction openTransaction() throws OrmException {
        if (!driver.supportsTransactions()) {
            throw new OrmTransactionException("The ORM driver does not support transactions");
        }
        if (currentTransaction != null) {
            if (currentTransaction.isOpen()) {
                throw new OrmTransactionException("A transaction is already open");
            }
        }
        currentTransaction = new SqlTransaction(driver, getConnection());
        return currentTransaction;
    }

    @Override
    public void close() {
    }

    @Override
    public <O> Table<O> tableFor(O pojo) throws OrmException {
        return tableFor((Class<O>) pojo.getClass());
    }

    @Override
    public <O> Table<O> tableFor(Class<O> type) throws OrmException {
        if (type == null) {
            throw new OrmException("Attempt to do table lookup for a null class");
        }
        if (tables.isEmpty()) {
            ServiceLoader<Database> svl = ServiceLoader.load(Database.class);
            for (Database database : svl) {
                for (Table<?> table : database.getTables()) {
                    tables.put(table.getObjectClass(), table);
                }
            }
        }
        Table<O> table = (Table<O>) tables.get(type);
        if (table == null) {
            throw new OrmException("Cannot find table for pojo of type " + type.getCanonicalName());
        }
        return table;
    }

    @Override
    public Selector selector() {
        return selector;
    }

    private <O> List<O> list(SelectPart<O> tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            return stream.collect(Collectors.toList());
        }
    }

    private <O> Stream<O> stream(ExecutablePart<O> tail) throws OrmException {
        List<? extends ExecutablePart<?>> queries = abstractionHelper.explodeAbstractions(tail);
        if (queries.isEmpty()) {
            throw new OrmException("Could not build query from parts. BUG!");
        }
        if (queries.size() == 1) {
            SelectPart<?> query = queries.get(0).getSelect();
            Stream<PojoCompare<O>> res = streamSingle(query.getTable(), queryHelper.buildSelectQuery(tail));
            return res.map(PojoCompare::getPojo);
        } else {
            if (driver.useUnionAll()) {
                Map<String, Table<O>> tableMap = queries.stream()
                        .map(query -> query.getSelect().getTable())
                        .collect(Collectors.toMap(table -> table.getObjectClass().getName(), table -> table));
                Stream<PojoCompare<O>> sorted = streamUnion(queryHelper.buildSelectUnionQuery(queries.stream().map(ExecutablePart::getSelect).collect(Collectors.toList())), tableMap)
                        .map(pojo -> new PojoCompare<O>(pops, tail.getSelect().getTable(), pojo))
                        .sorted(abstractionHelper.makeComparatorForTail(tail.getOrder()));
                return sorted.map(PojoCompare::getPojo);
            } else {
                Stream<PojoCompare<O>> res = queries.stream()
                        .flatMap(select -> {
                            try {
                                return streamSingle(queries.get(0).getSelect().getTable(), queryHelper.buildSelectQuery(select));
                            } catch (OrmException ex) {
                                throw new UncaughtOrmException(ex.getMessage(), ex);
                            }
                        });
                res = res.distinct();
                if (queries.size() > 1) {
                    if (!tail.getOrder().isEmpty()) {
                        res = res.sorted(abstractionHelper.makeComparatorForTail(tail.getOrder()));
                    } else {
                        res = res.sorted();
                    }
                }
                return res.map(PojoCompare::getPojo);
            }
        }
    }


    /**
     * Create a stream for the given query on the given table and return a
     * stream referencing the data.
     *
     * @param <O>   The type of the POJOs returned
     * @param table The table on which to query
     * @param query The SQL query
     * @return The stream of results.
     * @throws OrmException Thrown if there are SQL or ORM errors
     */
    private <O> Stream<PojoCompare<O>> streamSingle(Table<O> table, String query) throws OrmException {
        Connection con = getConnection();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            Stream<PojoCompare<O>> stream = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(new Iterator<PojoCompare<O>>() {
                                                            @Override
                                                            public boolean hasNext() {
                                                                try {
                                                                    return rs.next();
                                                                } catch (SQLException ex) {
                                                                    throw new UncaughtOrmException(ex.getMessage(), ex);
                                                                }
                                                            }

                                                            @Override
                                                            public PojoCompare<O> next() {
                                                                try {
                                                                    return new PojoCompare<>(pops, table, resultSetHelper.makePojoFromResultSet(rs, table));
                                                                } catch (OrmException ex) {
                                                                    throw new UncaughtOrmException(ex.getMessage(), ex);
                                                                }
                                                            }
                                                        },
                            Spliterator.ORDERED), false);
            return stream.onClose(() -> cleanup(con, stmt, rs));
        } catch (SQLException | UncaughtOrmException ex) {
            cleanup(con, null, null);
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    private <O> Stream<O> streamUnion(String query, Map<String, Table<O>> tables) throws OrmException {
        Connection con = getConnection();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            Stream<O> stream = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(new Iterator<O>() {
                        @Override
                        public boolean hasNext() {
                            try {
                                return rs.next();
                            } catch (SQLException ex) {
                                throw new UncaughtOrmException(ex.getMessage(), ex);
                            }
                        }

                        @Override
                        public O next() {
                            try {
                                return resultSetHelper.makePojoFromResultSet(rs, tables.get(rs.getString(QueryHelper.POJO_NAME_FIELD)));
                            } catch (OrmException | SQLException ex) {
                                throw new UncaughtOrmException(ex.getMessage(), ex);
                            }
                        }
                    }, Spliterator.ORDERED), false);
            return stream.onClose(() -> cleanup(con, stmt, rs));
        } catch (SQLException | UncaughtOrmException ex) {
            cleanup(con, null, null);
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    private <O> Optional<O> optional(SelectPart<O> tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            O one;
            Iterator<O> iterator = stream.iterator();
            if (iterator.hasNext()) {
                one = iterator.next();
            } else {
                return Optional.empty();
            }
            if (iterator.hasNext()) {
                throw new OrmException(format("Required one or none %s but found more than one", tail.getTable().getObjectClass().getSimpleName()));
            }
            return Optional.of(one);
        }
    }

    private <O> O one(SelectPart<O> tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            Iterator<O> iterator = stream.iterator();
            O one;
            if (iterator.hasNext()) {
                one = iterator.next();
            } else {
                throw new OrmException(format("Required exactly one %s but found none", tail.getTable().getObjectClass().getSimpleName()));
            }
            if (iterator.hasNext()) {
                throw new OrmException(format("Required exactly one %s but found more than one", tail.getTable().getObjectClass().getSimpleName()));
            }
            return one;
        }
    }

    /**
     * Obtain the SQL connection to use
     *
     * @return The connection
     */
    private Connection getConnection() {
        if (currentTransaction != null) {
            if (currentTransaction.isOpen()) {
                return currentTransaction.getConnection();
            }
            currentTransaction = null;
        }
        return connectionSupplier.get();
    }

    /**
     * Close a SQL Connection in a way that properly deals with transactions.
     *
     * @param con The SQL connection
     * @throws OrmException Thrown if there are SQL errors
     */
    private void closeConnection(Connection con) throws OrmException {
        if ((currentTransaction == null) || (currentTransaction.getConnection() != con)) {
            try {
                con.close();
            } catch (SQLException ex) {
                throw new OrmException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Cleanup SQL Connection, Statement and ResultSet insuring that
     * errors will not result in aborted cleanup.
     *
     * @param con The SQL connection
     * @param stmt The SQL statement
     * @param rs The SQL result set
     */
    private void cleanup(Connection con, Statement stmt, ResultSet rs) {
        Exception error = null;
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {
                error = ex;
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ex) {
                error = error != null ? error : ex;
            }
        }
        try {
            closeConnection(con);
        } catch (Exception ex) {
            error = error != null ? error : ex;
        }
        if (error != null) {
            throw new UncaughtOrmException(error.getMessage(), error);
        }
    }

    /**
     * Check if a table exists, and create if it does not
     *
     * @param table The table to check
     * @throws OrmException Thrown if there are SQL or ORM errors
     */
    private void checkTable(Table<?> table) throws OrmException {
        if (driver.createTables()) {
            if (!exists.containsKey(table)) {
                if (!tableExists(table)) {
                    Connection con = getConnection();
                    try (Statement stmt = con.createStatement()) {
                        stmt.executeUpdate(driver.getTableGenerator().generateSchema(table));
                    } catch (SQLException ex) {
                        throw new OrmSqlException(format("Error creating table (%s)", ex.getMessage()), ex);
                    } finally {
                        closeConnection(con);
                    }
                }
                exists.put(table, Boolean.TRUE);
            }
        }
    }

    /**
     * Get the full table name for a table.
     *
     * @param table The table
     * @return The table name
     * @throws OrmException Thrown if there are SQL or ORM errors
     */
    private String fullTableName(Table<?> table) throws OrmException {
        checkTable(table);
        return driver.fullTableName(table);
    }

    /**
     * Determine if the SQL table exists for a table structure
     *
     * @param table The Table
     * @return True if it exists in the database
     * @throws OrmException Thrown if there are SQL or ORM errors
     */
    private boolean tableExists(Table<?> table) throws OrmException {
        Connection con = getConnection();
        try {
            DatabaseMetaData dbm = con.getMetaData();
            try (ResultSet tables = dbm.getTables(driver.databaseName(table), null, driver.makeTableName(table), null)) {
                return tables.next();
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(format("Error checking table existence (%s)", ex.getMessage()), ex);
        } finally {
            closeConnection(con);
        }
    }

    /**
     * Get the unique field name for a field
     *
     * @param field The field
     * @return The ID
     */
    private String getUniqueFieldName(Field<?,?> field) {
        return format("%s_%s", field.getTable().getSqlTable(), field.getSqlName());
    }

}

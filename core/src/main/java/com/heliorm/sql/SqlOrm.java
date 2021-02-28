package com.heliorm.sql;

import com.heliorm.*;
import com.heliorm.def.Field;
import com.heliorm.def.Select;
import com.heliorm.impl.SelectPart;
import com.heliorm.def.Executable;
import com.heliorm.impl.Part;
import com.heliorm.impl.Selector;
import com.heliorm.query.Parser;

import java.sql.*;
import java.util.*;
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
    private final Map<Table, String> inserts = new ConcurrentHashMap<>();
    private final Map<Table, String> updates = new ConcurrentHashMap();
    private final Map<Table, String> deletes = new ConcurrentHashMap();
    private final Map<Table, Boolean> exists = new ConcurrentHashMap();
    private final QueryHelper queryHelper;
    private final AbstractionHelper abstractionHelper;
    private final PojoHelper pojoHelper;
    private final PreparedStatementHelper preparedStatementHelper;
    private final ResultSetHelper resultSetHelper;
    private SqlTransaction currentTransaction;
    private final Map<Field, String> fieldIds = new ConcurrentHashMap<>();


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
        this.queryHelper = new QueryHelper(driver, this::getFieldId, this::fullTableName);
        this.pojoHelper = new PojoHelper(pops);
        this.abstractionHelper = new AbstractionHelper();
        this.preparedStatementHelper = new PreparedStatementHelper(pojoHelper, driver::setEnum);
        this.resultSetHelper = new ResultSetHelper(pops, this::getFieldId);
        selector =  new Selector() {
            @Override
            public <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
                return SqlOrm.this.list(tail);
            }

            @Override
            public <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
                return SqlOrm.this.stream(tail);
            }

            @Override
            public <O, P extends Part & Executable> Optional<O> optional(P tail) throws OrmException {
                return SqlOrm.this.optional(tail);
            }

            @Override
            public <O, P extends Part & Executable> O one(P tail) throws OrmException {
                return SqlOrm.this.one(tail);
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
        O popo = (O) pops.newPojoInstance(table);
        for (Field field : table.getFields()) {
            pops.setValue(popo, field, pops.getValue(pojo, field));
        }
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int par = 1;
            for (Field field : table.getFields()) {
                if (field.isCollection()) {
                    continue;
                }
                if (field.isPrimaryKey()) {
                    if (field.isAutoNumber()) {
                        if (field.getFieldType() == Field.FieldType.STRING) {
                            if (pops.getValue(popo, field) == null) {
                                pops.setValue(popo, field, UUID.randomUUID().toString());
                            }
                            preparedStatementHelper.setValueInStatement(stmt, popo, field, par);
                            par++;
                        }
                    } else {
                        preparedStatementHelper.setValueInStatement(stmt, popo, field, par);
                        par++;
                    }
                } else {
                    preparedStatementHelper.setValueInStatement(stmt, popo, field, par);
                    par++;
                }
            }
            stmt.executeUpdate();
            Optional<Field> opt = table.getPrimaryKey();
            if (opt.isPresent()) {
                Field keyField = opt.get();
                if (keyField.isAutoNumber()) {
                    if (keyField.getFieldType() != Field.FieldType.STRING) {
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                pops.setValue(popo, keyField, driver.getKeyValueFromResultSet(rs, opt.get()));
                            }
                        }
                    }
                }
            }
            return popo;
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
            for (Field field : table.getFields()) {
                if (field.isCollection()) {
                    continue;
                }
                if (!field.isPrimaryKey()) {
                    preparedStatementHelper.setValueInStatement(stmt, pojo, field, par);
                    par++;
                }
            }
            Optional<Field> primaryKey = table.getPrimaryKey();
            if (primaryKey.isPresent()) {
                Object val = pojoHelper.getValueFromPojo(pojo, primaryKey.get());
                if (val == null) {
                    throw new OrmException(format("No value for key %s for %s in update", primaryKey.get().getJavaName(),  table.getObjectClass().getSimpleName()));
                }
                preparedStatementHelper.setValueInStatement(stmt, pojo, table.getPrimaryKey().get(), par);
            }
            else {
                throw new OrmException(format("No primary key for %s in update", table.getObjectClass().getSimpleName()));
            }
            int modified = stmt.executeUpdate();
            if (modified == 0) {
                throw new OrmException("The update did not modify any data");
            }
            return pojo;
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
            preparedStatementHelper.setValueInStatement(stmt, pojo, table.getPrimaryKey().get(), 1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        } finally {
            closeConnection(con);
        }
    }

    @Override
    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart<>(null, table, selector());
    }

    @Override
    public OrmTransaction openTransaction() throws OrmException {
         if (!driver.supportsTransactions()) {
            throw new OrmTransactionException("The ORM driver does not support transactions");
        }
        if (currentTransaction != null) {
            if (currentTransaction.isOpen()) {
                throw new OrmTransactionException(format("A transaction is already open"));
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
        return tableFor((Class<O>)pojo.getClass());
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
        Table<?> table = tables.get(type);
        if (table == null) {
            throw new OrmException("Cannot find table for pojo of type " + type.getCanonicalName());
        }
        return (Table<O>) table;
    }

    @Override
    public final Selector selector() {
        return selector;
     }

    private <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            return stream.collect(Collectors.toList());
        }
    }

    private <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
        List<List<Part>> queries = abstractionHelper.explodeAbstractions(tail);
        if (queries.isEmpty()) {
            throw new OrmException("Could not build query from parts. BUG!");
        }
        if (queries.size() == 1) {
            List<Part> parts = queries.get(0);
            Stream<PojoCompare<O>> res = streamSingle(parts.get(0).getReturnTable(), queryHelper.buildSelectQuery(Parser.parse(parts)));
            return res.map(pojoCompare -> pojoCompare.getPojo());
        } else {
            if (driver.useUnionAll()) {
                Map<String, Table<O>> tableMap = queries.stream()
                        .map(parts -> parts.get(0).getReturnTable())
                        .collect(Collectors.toMap(table -> table.getObjectClass().getName(), table -> table));
                return streamUnion(queryHelper.buildSelectUnionQuery(queries), tableMap);
            } else {
                Stream<PojoCompare<O>> res = queries.stream()
                        .flatMap(parts -> {
                            try {
                                return streamSingle(parts.get(0).getReturnTable(), queryHelper.buildSelectQuery(Parser.parse(parts)));
                            } catch (OrmException ex) {
                                throw new UncaughtOrmException(ex.getMessage(), ex);
                            }
                        });
                res = res.distinct();
                if (queries.size() > 1) {
                    if (tail.getType() == Part.Type.ORDER) {
                        res = res.sorted(abstractionHelper.makeComparatorForTail(tail));
                    } else {
                        res = res.sorted();
                    }
                }
                return res.map(pojoCompare -> pojoCompare.getPojo());
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
     * @throws OrmException
     */
    private <O, P extends Part & Executable> Stream<PojoCompare<O>> streamSingle(Table<O> table, String query) throws OrmException {
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
                                                                    return new PojoCompare(pops, table, resultSetHelper.makePojoFromResultSet(rs, table));
                                                                } catch (OrmException ex) {
                                                                    throw new UncaughtOrmException(ex.getMessage(), ex);
                                                                }
                                                            }
                                                        },
                            Spliterator.ORDERED), false);
            stream.onClose(() -> {
                cleanup(con, stmt, rs);
            });
            return stream;
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
                                return resultSetHelper.makePojoFromResultSet(rs, tables.get(rs.getString(queryHelper.POJO_NAME_FIELD)));
                            } catch (OrmException | SQLException ex) {
                                throw new UncaughtOrmException(ex.getMessage(), ex);
                            }
                        }
                    }, Spliterator.ORDERED), false);
            stream.onClose(() -> {
                cleanup(con, stmt, rs);
            });
            return stream;
        } catch (SQLException | UncaughtOrmException ex) {
            cleanup(con, null, null);
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    private <O, P extends Part & Executable> Optional<O> optional(P tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            O one;
            Iterator<O> iterator = stream.iterator();
            if (iterator.hasNext()) {
                one = iterator.next();
            } else {
                return Optional.empty();
            }
            if (iterator.hasNext()) {
                throw new OrmException(format("Required one or none %s but found more than one", tail.getReturnTable().getObjectClass().getSimpleName()));
            }
            return Optional.of(one);
        }
    }

    private <O, P extends Part & Executable> O one(P tail) throws OrmException {
        try (Stream<O> stream = stream(tail)) {
            Iterator<O> iterator = stream.iterator();
            O one;
            if (iterator.hasNext()) {
                one = iterator.next();
            } else {
                throw new OrmException(format("Required exactly one %s but found none", tail.getReturnTable().getObjectClass().getSimpleName()));
            }
            if (iterator.hasNext()) {
                throw new OrmException(format("Required exactly one %s but found more than one", tail.getReturnTable().getObjectClass().getSimpleName()));
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

    /** Close a SQL Connection in a way that properly deals with transactions.
     *
     * @param con
     * @throws OrmException
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
    /** Cleanup SQL Connection, Statement and ResultSet insuring that
     * errors will not result in aborted cleanup.
     *
     * @param con
     * @param stmt
     * @param rs
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

    /** Check if a table exists, and create if if it does not
     *
     * @param table
     * @throws OrmException
     */
    private void checkTable(Table table) throws OrmException {
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

    /** Get the full table name for a table.
     *
     * @param table The table
     * @return The table name
     * @throws OrmException
     */
    private String fullTableName(Table table) throws OrmException {
        checkTable(table);
        return driver.fullTableName(table);
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
            try (ResultSet tables = dbm.getTables(driver.databaseName(table), null, driver.makeTableName(table), null)) {
                return tables.next();
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(format("Error checking table existance (%s)", ex.getMessage()), ex);
        } finally {
            closeConnection(con);
        }
    }

    /** Get the unique field ID for a field
     *
     * @param field The field
     * @return The ID
     */
    private String getFieldId(Field field) {
        return fieldIds.computeIfAbsent(field, k -> {
            String uuid;
            do { // very simple collison avoidance
                uuid = UUID.randomUUID().toString().substring(0, 8);
            }
            while (fieldIds.containsKey(uuid));
            return uuid;
        });
    }

}

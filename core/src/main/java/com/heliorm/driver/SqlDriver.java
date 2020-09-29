package com.heliorm.driver;

import com.heliorm.Database;
import com.heliorm.OrmDriver;
import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;
import com.heliorm.OrmTransactionDriver;
import com.heliorm.OrmTransactionException;
import com.heliorm.PojoOperations;
import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Executable;
import com.heliorm.def.Field;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.OrderedPart;
import com.heliorm.impl.Part;
import com.heliorm.impl.SelectPart;
import com.heliorm.query.AndCriteria;
import com.heliorm.query.Criteria;
import com.heliorm.query.Link;
import com.heliorm.query.ListCriteria;
import com.heliorm.query.OrCriteria;
import com.heliorm.query.Order;
import com.heliorm.query.Parser;
import com.heliorm.query.Query;
import com.heliorm.query.TableSpec;
import com.heliorm.query.ValueCriteria;
;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

/**
 * @author gideon
 */
public abstract class SqlDriver implements OrmDriver, OrmTransactionDriver {

    private static final String POJO_NAME_FIELD = "pojo_field_name";
    private final Supplier<Connection> connectionSupplier;
    private SqlTransaction currentTransaction;
    private boolean createTables = false;
    private boolean rollbackOnUncommittedClose = false;
    private final Map<Table, String> inserts = new HashMap();
    private final Map<Table, String> updates = new HashMap();
    private final Map<Table, String> deletes = new HashMap();
    private final Map<Table, Boolean> exists = new HashMap();
    private final PojoOperations pops;
    private final Map<Database, Database> aliases;

    public SqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops) {
        this(connectionSupplier, pops, Collections.EMPTY_MAP);
    }

    public SqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops, Map<Database, Database> aliases) {
        this.connectionSupplier = connectionSupplier;
        this.pops = pops;
        this.aliases = aliases;
    }

    @Override
    public <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
        List<List<Part>> queries = explodeAbstractions(tailToList(tail));
        if (queries.isEmpty()) {
            throw new OrmException("Could not build query from parts. BUG!");
        }
        if (queries.size() == 1) {
            List<Part> parts = queries.get(0);
            Stream<PojoCompare<O>> res = streamSingle(parts.get(0).getReturnTable(), buildSelectQuery(Parser.parse(parts)));
            return res.map(pojoCompare -> pojoCompare.getPojo());
        } else {
            if (useUnionAll()) {
                Map<String, Table<O>> tableMap = queries.stream()
                        .map(parts -> parts.get(0).getReturnTable())
                        .collect(Collectors.toMap(table -> table.getObjectClass().getName(), table -> table));
                return streamUnion(buildSelectUnionQuery(queries), tableMap);
            } else {
                Stream<PojoCompare<O>> res = queries.stream()
                        .flatMap(parts -> {
                            try {
                                return streamSingle(parts.get(0).getReturnTable(), buildSelectQuery(Parser.parse(parts)));
                            } catch (OrmException ex) {
                                throw new UncaughtOrmException(ex.getMessage(), ex);
                            }
                        });
                res = res.distinct();
                if (queries.size() > 1) {
                    if (tail.getType() == Part.Type.ORDER) {
                        res = res.sorted(makeComparatorForTail(tail));
                    } else {
                        res = res.sorted();
                    }
                }
                return res.map(pojoCompare -> pojoCompare.getPojo());
            }
        }
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

    private String buildSelectUnionQuery(List<List<Part>> queries) throws OrmException {
        Set<String> allFields = queries.stream()
                .map(parts -> parts.get(0).getReturnTable())
                .flatMap(table -> (Stream<Field>) (table.getFields().stream()))
                .map(field -> field.getSqlName())
                .collect(Collectors.toSet());
        StringJoiner buf = new StringJoiner(" UNION ALL ");
        Query root = null;
        for (List<Part> parts : queries) {
            root = Parser.parse(parts);
            StringBuilder tablesQuery = new StringBuilder();
            StringJoiner fieldsQuery = new StringJoiner(",");
            Map<String, Field> tableFields = root.getTable().getFields().stream()
                    .collect(Collectors.toMap(field -> field.getSqlName(), field -> field));
            for (String name : allFields) {
                if (tableFields.containsKey(name)) {
                    fieldsQuery.add(fieldName(root.getTable(), tableFields.get(name)));
                } else {
                    fieldsQuery.add(format("NULL AS %s", virtualFieldName(name)));
                }
            }
            tablesQuery.append(format("SELECT %s", fieldsQuery.toString()));
            tablesQuery.append(format(",%s AS %s", virtualValue(root.getTable().getObjectClass().getName()), virtualFieldName(POJO_NAME_FIELD)));
            tablesQuery.append(format(" FROM %s", fullTableName(root.getTable()), fullTableName(root.getTable())));
            StringBuilder whereQuery = new StringBuilder();
            Optional<Criteria> optCrit = root.getCriteria();
            if (optCrit.isPresent()) {
                whereQuery.append(expandCriteria(root, optCrit.get()));

            }
            Optional<Link> optLink = root.getLink();
            if (optLink.isPresent()) {
                tablesQuery.append(expandLinkTables(root, optLink.get()));
                if (whereQuery.length() > 0) {
                    whereQuery.append(" AND ");
                }
                whereQuery.append(expandLinkWheres(optLink.get()));
            }
            if (whereQuery.length() > 0) {
                tablesQuery.append(" WHERE ");
                tablesQuery.append(whereQuery);
            }
            buf.add(tablesQuery.toString());
        }
        StringBuilder query = new StringBuilder(buf.toString());
        // do ordering
        Optional<Order> optOrder = root.getOrder();
        if (optOrder.isPresent()) {
            query.append(" ORDER BY ");
            query.append(expandOrder(root, optOrder.get()));
        }
        return query.toString();
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
                                                                    return new PojoCompare(pops, table, makePojoFromResultSet(rs, table));
                                                                } catch (OrmException ex) {
                                                                    throw new UncaughtOrmException(ex.getMessage(), ex);
                                                                }
                                                            }
                                                        },
                            Spliterator.ORDERED), false);
            stream.onClose(() -> {
                try {
                    rs.close();
                    close(con);
                } catch (SQLException | OrmException ex) {
                    throw new UncaughtOrmException(ex.getMessage(), ex);
                }
            });
            return stream;
        } catch (SQLException | UncaughtOrmException ex) {
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
                                return makePojoFromResultSet(rs, tables.get(rs.getString(POJO_NAME_FIELD)));
                            } catch (OrmException | SQLException ex) {
                                throw new UncaughtOrmException(ex.getMessage(), ex);
                            }
                        }
                    }, Spliterator.ORDERED), false);
            stream.onClose(() -> {
                try {
                    rs.close();
                    close(con);
                } catch (SQLException | OrmException ex) {
                    throw new UncaughtOrmException(ex.getMessage(), ex);
                }
            });
            return stream;
        } catch (SQLException | UncaughtOrmException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    @Override
    public <T extends Table<O>, O> O create(T table, O pojo) throws OrmException {
        String query = inserts.get(table);
        if (query == null) {
            query = buildInsertQuery(table);
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
                if (field.isPrimaryKey()) {
                    if (field.isAutoNumber()) {
                        if (field.getFieldType() == Field.FieldType.STRING) {
                            pops.setValue(popo, field, UUID.randomUUID().toString());
                            setValueInStatement(stmt, popo, field, par);
                            par++;
                        }
                    } else {
                        setValueInStatement(stmt, popo, field, par);
                        par++;
                    }
                } else {
                    setValueInStatement(stmt, popo, field, par);
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
                                pops.setValue(popo, keyField, getKeyValueFromResultSet(rs, opt.get()));
                            }
                        }
                    }
                }
            }
            return popo;
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        } finally {
            close(con);
        }
    }

    public <T extends Table<O>, O> O update(T table, O pojo) throws OrmException {
        String query = updates.get(table);
        if (query == null) {
            query = buildUpdateQuery(table);
            updates.put(table, query);
        }
        Connection con = getConnection();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            int par = 1;
            for (Field field : table.getFields()) {
                if (!field.isPrimaryKey()) {
                    setValueInStatement(stmt, pojo, field, par);
                    par++;
                }
            }
            setValueInStatement(stmt, pojo, table.getPrimaryKey().get(), par);
            stmt.executeUpdate();
            return pojo;
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        } finally {
            close(con);
        }   

    }

    @Override
    public <T extends Table<O>, O> void delete(T table, O pojo) throws OrmException {
        String query = deletes.get(table);
        if (query == null) {
            query = buildDeleteQuery(table);
            deletes.put(table, query);
        }
        Connection con = getConnection();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            setValueInStatement(stmt, pojo, table.getPrimaryKey().get(), 1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        } finally {
            close(con);
        }
    }

    protected void setEnum(PreparedStatement stmt, int par, String value) throws SQLException {
        stmt.setString(par, value);
    }

    protected abstract boolean useUnionAll();

    private String buildInsertQuery(Table<?> table) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("INSERT INTO %s(", fullTableName(table)));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            if (field.isPrimaryKey()) {
                if (field.isAutoNumber()) {
                    if (field.getFieldType() != Field.FieldType.STRING) {
                        continue;
                    }
                }
            }
            fields.add(format("%s", fieldName(table, field)));
            values.add("?");
        }
        query.append(fields.toString());
        query.append(") VALUES(");
        query.append(values.toString());
        query.append(")");
        return query.toString();
    }

    private String buildUpdateQuery(Table<?> table) throws OrmException {
        if (!table.getPrimaryKey().isPresent()) {
            throw new OrmException("A table needs primary key for objects to be updated");
        }
        StringBuilder query = new StringBuilder();
        query.append(format("UPDATE %s SET ", fullTableName(table)));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            if (!field.isPrimaryKey()) {
                fields.add(format("%s=?", fieldName(table, field)));
            }
        }
        query.append(fields.toString());
        query.append(format(" WHERE %s=?", fieldName(table, table.getPrimaryKey().get())));
        return query.toString();
    }

    private String buildDeleteQuery(Table<?> table) throws OrmException {
        if (table.getPrimaryKey().isPresent()) {
            return format("DELETE FROM %s WHERE %s=?", fullTableName(table), fieldName(table, table.getPrimaryKey().get()));
        } else {
            throw new OrmException("A table needs primary key for objects to be deleted");
        }
    }

    private String buildSelectQuery(Query root) throws OrmException {
        StringBuilder tablesQuery = new StringBuilder();
        tablesQuery.append(format("SELECT DISTINCT %s.* FROM %s", fullTableName(root.getTable()), fullTableName(root.getTable())));
        StringBuilder whereQuery = new StringBuilder();
        Optional<Criteria> optCrit = root.getCriteria();
        if (optCrit.isPresent()) {
            whereQuery.append(expandCriteria(root, optCrit.get()));

        }
        Optional<Link> optLink = root.getLink();
        if (optLink.isPresent()) {
            tablesQuery.append(expandLinkTables(root, optLink.get()));
            if (whereQuery.length() > 0) {
                whereQuery.append(" AND ");
            }
            whereQuery.append(expandLinkWheres(optLink.get()));
        }
        // finalize the query
        StringBuilder query = new StringBuilder();
        query.append(tablesQuery);
        if (whereQuery.length() > 0) {
            query.append(" WHERE ");
            query.append(whereQuery);
        }
        // do ordering
        Optional<Order> optOrder = root.getOrder();
        if (optOrder.isPresent()) {
            query.append(" ORDER BY ");
            query.append(expandOrder(root, optOrder.get()));
        }
        return query.toString();
    }

    private String expandLinkTables(TableSpec left, Link right) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format(" JOIN %s ON %s=%s ",
                fullTableName(right.getTable()),
                fullFieldName(left.getTable(), right.getLeftField()),
                fullFieldName(right.getTable(), right.getField())));
        if (right.getLink().isPresent()) {
            query.append(expandLinkTables(right, right.getLink().get()));
        }
        return query.toString();
    }

    private String expandLinkWheres(Link link) throws OrmException {
        StringBuilder query = new StringBuilder();
        Optional<Criteria> optCrit = link.getCriteria();
        if (optCrit.isPresent()) {
            query.append(expandCriteria(link, optCrit.get()));
        }
        if (link.getLink().isPresent()) {
            query.append(expandLinkWheres(link.getLink().get()));
        }
        return query.toString();

    }

    private String expandCriteria(TableSpec table, Criteria crit) throws OrmException {
        switch (crit.getType()) {
            case LIST_FIELD:
                return expandListFieldCriteria(table, (ListCriteria) crit);
            case VALUE_FIELD:
                return expandValueFieldCriteria(table, (ValueCriteria) crit);
            case AND:
                AndCriteria and = (AndCriteria) crit;
                return format("(%s AND %s)", expandCriteria(table, and.getLeft()), expandCriteria(table, and.getRight()));
            case OR:
                OrCriteria or = (OrCriteria) crit;
                return format("(%s OR %s)", expandCriteria(table, or.getLeft()), expandCriteria(table, or.getRight()));
            default:
                throw new OrmException(format("Unexpected criteria type '%s' in switch. BUG!", crit.getType()));
        }
    }

    private String expandListFieldCriteria(TableSpec table, ListCriteria crit) throws OrmException {
        StringJoiner list = new StringJoiner(",");
        for (Object val : crit.getValues()) {
            list.add(format("'%s'", sqlValue(val)));
        }
        return format("%s %s (%s)", fullFieldName(table.getTable(), crit.getField()), listOperator(crit), list.toString());
    }

    private String expandValueFieldCriteria(TableSpec table, ValueCriteria crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s%s'%s'", fullFieldName(table.getTable(), crit.getField()), valueOperator(crit), sqlValue(crit.getValue())
        ));
        return query.toString();
    }

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
//        query.append(format("%s", fullFieldName(table.getTable(), order.getField())));
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
     * Creates a Pojo for the given table from the element currently at the
     * result set cursor.
     *
     * @param <O>   The type of Pojo
     * @param rs    The result set
     * @param table The table
     * @return The pojo
     * @throws OrmException Thrown if there is an error building the Pojo.
     */
    private <O> O makePojoFromResultSet(ResultSet rs, Table<O> table) throws OrmException {
        try {
            O pojo = (O) pops.newPojoInstance(table);
            for (Field field : table.getFields()) {
                setValueInPojo(pojo, field, rs);
            }
            return pojo;
        } catch (OrmException ex) {
            throw new OrmException(format("Error reading table %s (%s)", table.getSqlTable(), ex.getMessage()), ex);
        }
    }

    private void setValueInPojo(Object pojo, Field field, ResultSet rs) throws OrmException {
        switch (field.getFieldType()) {
            case LONG:
            case INTEGER:
            case SHORT:
            case BYTE:
            case DOUBLE:
            case FLOAT:
            case BOOLEAN:
            case ENUM:
            case STRING:
            case DATE:
                pops.setValue(pojo, field, getValueFromResultSet(rs, field));
                break;
            case TIMESTAMP:
                pops.setValue(pojo, field, getTimestampFromSql(rs, field));
                break;
            case DURATION:
                pops.setValue(pojo, field, getDurationFromSql(rs, field));
                break;
            default:
                throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
        }
    }

    /**
     * Set the value in a prepared statement to the value of the given field
     * from the given POJO
     *
     * @param stmt  The prepared statement in which to set the value
     * @param pojo  The POJO from which to obtain the value
     * @param field The field for which to get the value from the POJO
     * @param par   The position in the prepared statement for the value
     * @throws OrmException
     */
    private void setValueInStatement(PreparedStatement stmt, Object pojo, Field field, int par) throws OrmException {
        try {
            switch (field.getFieldType()) {
                case LONG:
                case INTEGER:
                case SHORT:
                case BYTE:
                case DOUBLE:
                case FLOAT:
                case BOOLEAN:
                    stmt.setObject(par, getValueFromPojo(pojo, field));
                    break;
                case ENUM:
                    setEnum(stmt, par, getStringFromPojo(pojo, field));
                    break;
                case STRING:
                    stmt.setString(par, getStringFromPojo(pojo, field));
                    break;
                case DATE:
                    stmt.setDate(par, getDateFromPojo(pojo, field));
                    break;
                case TIMESTAMP:
                    stmt.setTimestamp(par, getTimestampFromPojo(pojo, field));
                    break;
                case DURATION:
                    stmt.setString(par, getDurationFromPojo(pojo, field));
                    break;
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    private String sqlValue(Object object) {
        return object.toString();
    }

    /**
     * Return the SQL operator for the given list criteria part's operator.
     *
     * @param part The part
     * @return The operator
     * @throws OrmException
     */
    private String listOperator(ListCriteria part) throws OrmException {
        switch (part.getOperator()) {
            case IN:
                return " IN";
            case NOT_IN:
                return " NOT IN";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", part.getOperator()));
        }
    }

    /**
     * Return the SQL operator for the given value criteria part's operator.
     *
     * @param part The part
     * @return The operator
     * @throws OrmException
     */
    private String valueOperator(ValueCriteria part) throws OrmException {
        switch (part.getOperator()) {
            case EQ:
                return "=";
            case NOT_EQ:
                return "<>";
            case GE:
                return ">=";
            case LE:
                return "<=";
            case GT:
                return ">";
            case LT:
                return "<";
            case LIKE:
                return " LIKE ";
            case NOT_LIKE:
                return " NOT LIKE ";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", part.getOperator()));
        }
    }

    /**
     * Extract the value for the given field from a SQL result set.
     *
     * @param rs    The result set
     * @param field The field for which we're reading data
     * @return The data
     * @throws OrmException Thrown if we cannot work out how to extract the
     *                      data.
     */
    private Object getValueFromResultSet(ResultSet rs, Field field) throws OrmException {
        try {
            String column = field.getSqlName();
            switch (field.getFieldType()) {
                case LONG:
                    return rs.getLong(column);
                case INTEGER:
                    return rs.getInt(column);
                case SHORT:
                    return rs.getShort(column);
                case BYTE:
                    return rs.getByte(column);
                case DOUBLE:
                    return rs.getDouble(column);
                case FLOAT:
                    return rs.getFloat(column);
                case BOOLEAN:
                    return rs.getBoolean(column);
                case ENUM: {
                    Class javaType = field.getJavaType();
                    if (!javaType.isEnum()) {
                        throw new OrmException(format("Field %s is not an enum. BUG!", field.getJavaName()));
                    }
                    String val = rs.getString(column);
                    if (val != null) {
                        return Enum.valueOf(javaType, val);
                    }
                    return null;
                }
                case STRING:
                    return rs.getString(column);
                case DATE:
                    return rs.getDate(column);
                case TIMESTAMP:
                    return rs.getTimestamp(column);
                case DURATION: {
                    Class javaType = field.getJavaType();
                    if (!Duration.class.isAssignableFrom(javaType)) {
                        throw new OrmException(format("Field %s is not a duration. BUG!", field.getJavaName()));
                    }
                    String val = rs.getString(column);
                    if (val != null) {
                        try {
                            return Duration.parse(val);
                        } catch (DateTimeParseException ex) {
                            throw new OrmException(format("Cannot parse text to a duration (%s)", ex.getMessage()), ex);
                        }
                    }
                    return null;
                }
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
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
     * Get a value from the given POJO for the given field as an object
     *
     * @param pojo  The POJO from which to read the object.
     * @param field The field to read.
     * @return The object value.
     * @throws OrmException
     */
    private Object getValueFromPojo(Object pojo, Field field) throws OrmException {
        return pops.getValue(pojo, field);
    }

    /**
     * Get a string value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the string.
     * @param field The field to read.
     * @return The string value.
     * @throws OrmException
     */
    private String getStringFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            throw new OrmException(format("Could not read String value for field type '%s'.", field.getFieldType()));
        }
        return (String) value;
    }

    /**
     * Get a timestamp value from SQL for the given POJO and field
     *
     * @param rs    The ResultSet
     * @param field The field
     * @return The correct value
     */
    private Instant getTimestampFromSql(ResultSet rs, Field field) throws OrmException {
        try {
            Timestamp value = rs.getTimestamp(field.getSqlName());
            if (value == null) {
                return null;
            }
            if (!(value instanceof Timestamp)) {
                throw new OrmException(format("Could not read Timestamp value from SQL for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
            }
            return ((Timestamp) value).toInstant();
        } catch (SQLException ex) {
            throw new OrmException(format("Could not read timestamp value from SQL (%s)", ex.getMessage()), ex);
        }
    }

    /**
     * Get a duration value from SQL for the given POJO and field
     *
     * @param rs    The ResultSet
     * @param field The field
     * @return The correct value
     */
    private Duration getDurationFromSql(ResultSet rs, Field field) throws OrmException {
        try {
            String value = rs.getString(field.getSqlName());
            if (value == null) {
                return null;
            }
            return Duration.parse(value);
        } catch (SQLException ex) {
            throw new OrmException(format("Could not read duration value from SQL (%s)", ex.getMessage()), ex);
        }
    }

    /**
     * Get a date value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the date.
     * @param field The field to read.
     * @return The date value.
     * @throws OrmException
     */
    private java.sql.Date getDateFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof java.util.Date)) {
            throw new OrmException(format("Could not read Date value for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
        }
        return new java.sql.Date(((java.util.Date) value).getTime());
    }

    /**
     * Get a timestamp value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the timestamp.
     * @param field The field to read.
     * @return The timestamp value.
     * @throws OrmException
     */
    private java.sql.Timestamp getTimestampFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (value instanceof Instant) {
            return new java.sql.Timestamp(((Instant) value).toEpochMilli());
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }
        throw new OrmException(format("Could not read Instant value for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
    }

    /**
     * Get a duration value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the timestamp.
     * @param field The field to read.
     * @return The duration string value.
     * @throws OrmException
     */
    private String getDurationFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (value instanceof Duration) {
            return ((Duration) value).toString();
        }
        throw new OrmException(format("Could not read Duration value for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
    }

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

    private void close(Connection con) throws OrmException {
        if ((currentTransaction == null) || (currentTransaction.getConnection() != con)) {
            try {
                con.close();
            } catch (SQLException ex) {
                throw new OrmException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Create a comparator based on the tail of the query that will compare
     * Pojos on their fields.
     *
     * @param <O>
     * @param <P>
     * @param tail
     * @return
     */
    private <O, P extends Part> Comparator<PojoCompare<O>> makeComparatorForTail(P tail) {
        List<OrderedPart> order = new LinkedList();
        while (tail.getType() == Part.Type.ORDER) {
            order.add(0, (OrderedPart) tail);
            tail = (P) tail.left();
        }
        List<Comparator<PojoCompare<O>>> comps = new LinkedList();
        for (OrderedPart op : order) {
            comps.add((Comparator<PojoCompare<O>>) (PojoCompare<O> w1, PojoCompare<O> w2) -> {
                if (op.getDirection() == OrderedPart.Direction.ASCENDING) {
                    return w1.compareTo(w2, op.getField());
                } else {
                    return w2.compareTo(w1, op.getField());

                }
            });
        }
        return new CompoundComparator(comps);
    }

    /**
     * Convert the supplied part chain to a list of sequenctial parts. This is
     * required to pass the list of parts to the parser so it can generate a
     * query strucutre.
     *
     * @param tail The tail part
     * @return The list of parts
     */
    private List<Part> tailToList(Part tail) {
        List<Part> parts = new ArrayList();
        Part part = tail.head();
        while (part != null) {
            parts.add(part);
            part = part.right();
        }
        return parts;
    }

    /* Expand the query parts in the list so that the query branches into
     * multiple table queries when a select or join with a table which has
     * sub-tables is encountered.
     * @param parts the query parts
     * @return The expanded query parts lists.
     */
    private List<List<Part>> explodeAbstractions(List<Part> parts) {
        return explode(parts, 0);
    }

    /**
     * Expand the query parts in the list so that the query branches into
     * multiple table queries when a select or join with a table which has
     * sub-tables is encountered.
     *
     * @param parts the query parts
     * @param idx   the index of the part being examined
     * @return The expanded query parts lists.
     */
    private List<List<Part>> explode(List<Part> parts, int idx) {
        Part part = parts.get(idx);
        List<List<Part>> res = new LinkedList();
        if ((part.getType() == Part.Type.SELECT) || (part.getType() == Part.Type.JOIN)) {
            Table<?> table = part.getSelectTable();
            Set<Table<?>> subTables = table.getSubTables();
            if (!subTables.isEmpty()) {
                for (Table<?> subTable : subTables) {
                    List<Part> copy = new ArrayList(parts);
                    Part old = copy.remove(idx);
                    Part left = null;
                    if (idx > 0) {
                        left = old.left();
                    }
                    Part newPart;
                    if (part.getType() == Part.Type.SELECT) {
                        copy.add(idx, new SelectPart(left, subTable));
                    } else {
                        copy.add(idx, new JoinPart(left, subTable));
                    }
                    if (idx < parts.size() - 1) {
                        res.addAll(explode(copy, idx + 1));
                    } else {
                        res.add(copy);
                    }
                }
                return res;
            }
        }
        if (idx < parts.size() - 1) {
            res.addAll(explode(parts, idx + 1));
        } else {
            res.add(parts);
        }
        return res;
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

}
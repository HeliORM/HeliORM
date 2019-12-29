package net.legrange.orm.driver;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.legrange.orm.Database;
import net.legrange.orm.OrmException;
import net.legrange.orm.OrmMetaDriver;
import net.legrange.orm.OrmTransaction;
import net.legrange.orm.OrmTransactionException;
import net.legrange.orm.PojoOperations;
import net.legrange.orm.Table;
import net.legrange.orm.UncaughtOrmException;
import net.legrange.orm.OrmTransactionDriver;
import net.legrange.orm.def.Executable;
import net.legrange.orm.def.Field;
import net.legrange.orm.impl.JoinPart;
import net.legrange.orm.impl.OrderedPart;
import net.legrange.orm.impl.Part;
import net.legrange.orm.impl.SelectPart;
import net.legrange.orm.rep.AndCriteria;
import net.legrange.orm.rep.Criteria;
import net.legrange.orm.rep.Link;
import net.legrange.orm.rep.ListCriteria;
import net.legrange.orm.rep.OrCriteria;
import net.legrange.orm.rep.Order;
import net.legrange.orm.rep.Parser;
import net.legrange.orm.rep.Query;
import net.legrange.orm.rep.TableSpec;
import net.legrange.orm.rep.ValueCriteria;

/**
 * @author gideon
 */
public abstract class SqlDriver implements OrmMetaDriver, OrmTransactionDriver {

    private final Supplier<Connection> connectionSupplier;
    private SqlTransaction currentTransaction;
    private boolean rollbackOnUncommittedClose = false;
    private final Map<Table, PreparedStatement> inserts = new HashMap();
    private final Map<Table, PreparedStatement> updates = new HashMap();
    private final Map<Table, PreparedStatement> deletes = new HashMap();
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
        Stream<Wrapper<O>> res = queries.stream()
                .flatMap(parts -> {
                    try {
                        return streamSingle(parts.get(0).getReturnTable(), buildSelectQuery(Parser.parse(parts)));
                    } catch (OrmException ex) {
                        throw new UncaughtOrmException(ex.getMessage(), ex);
                    }
                });
        if (queries.size() > 1) {
            if (tail.getType() == Part.Type.ORDER) {
                res = res.sorted(makeComparatorForTail(tail));
            } else {
                res = res.sorted();
            }
        }
        return res.distinct().map(wrapper -> wrapper.getPojo());
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
    public void setRollbackOnUncommittedClose(boolean rollback) {
         rollbackOnUncommittedClose = rollback;
    }

    boolean getRollbackOnUncommittedClose() {
        return rollbackOnUncommittedClose;
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
    private <O, P extends Part & Executable> Stream<Wrapper<O>> streamSingle(Table<O> table, String query) throws OrmException {
        Connection sql = getConnection();
        try {
            Statement stmt = sql.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            Stream<Wrapper<O>> stream = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(new Iterator<Wrapper<O>>() {
                                                            @Override
                                                            public boolean hasNext() {
                                                                try {
                                                                    return rs.next();
                                                                } catch (SQLException ex) {
                                                                    throw new UncaughtOrmException(ex.getMessage(), ex);
                                                                }
                                                            }

                                                            @Override
                                                            public Wrapper<O> next() {
                                                                try {
                                                                    return new Wrapper(pops, table, makePojoFromResultSet(rs, table));
                                                                } catch (OrmException ex) {
                                                                    throw new UncaughtOrmException(ex.getMessage(), ex);
                                                                }
                                                            }
                                                        },
                            Spliterator.ORDERED), false);
            stream.onClose(() -> {
                try {
                    rs.close();
                } catch (SQLException ex) {
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
        try {
            PreparedStatement stmt = inserts.get(table);
            if (stmt == null) {
                stmt = getConnection().prepareStatement(buildInsertQuery(table), Statement.RETURN_GENERATED_KEYS);
                inserts.put(table, stmt);
            }
            int par = 1;
            for (Field field : table.getFields()) {
                if (field.isPrimaryKey()) {
                    if (field.isAutoNumber()) {
                        if (field.getFieldType() == Field.FieldType.STRING) {
                            pops.setValue(pojo, field, UUID.randomUUID().toString());
                            setValueInStatement(stmt, pojo, field, par);
                        } else {
                            stmt.setObject(par, null);
                        }
                    } else {
                        setValueInStatement(stmt, pojo, field, par);
                    }
                } else {
                    setValueInStatement(stmt, pojo, field, par);
                }
                par++;
            }
            stmt.executeUpdate();
            Optional<Field> opt = table.getPrimaryKey();
            if (opt.isPresent()) {
                Field keyField = opt.get();
                if (keyField.isAutoNumber()) {
                    if (keyField.getFieldType() != Field.FieldType.STRING) {
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                pops.setValue(pojo, keyField, getKeyValueFromResultSet(rs, opt.get()));
                            }
                        }
                    }
                }
            }
            return pojo;
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    @Override
    public <T extends Table<O>, O> O update(T table, O pojo) throws OrmException {
        try {
            PreparedStatement stmt = updates.get(table);
            if (stmt == null) {
                stmt = getConnection().prepareStatement(buildUpdateQuery(table));
                updates.put(table, stmt);
            }
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
        }
    }

    @Override
    public <T extends Table<O>, O> void delete(T table, O pojo) throws OrmException {
        try {
            PreparedStatement stmt = deletes.get(table);
            if (stmt == null) {
                stmt = getConnection().prepareStatement(buildDeleteQuery(table));
                deletes.put(table, stmt);
            }
            setValueInStatement(stmt, pojo, table.getPrimaryKey().get(), 1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean tableExists(Table table) throws OrmException {
        try {
            DatabaseMetaData metaData = getConnection().getMetaData();
            try (ResultSet tables = metaData.getTables(databaseName(table), null, tableName(table), new String[]{"TABLE"})) {
                return tables.next();
            }
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    protected String buildInsertQuery(Table<?> table) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("INSERT INTO %s(", fullTableName(table)));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            fields.add(field.getSqlName());
            values.add("?");
        }
        query.append(fields.toString());
        query.append(") VALUES(");
        query.append(values.toString());
        query.append(")");
        return query.toString();
    }

    protected String buildUpdateQuery(Table<?> table) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("UPDATE %s SET ", fullTableName(table)));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            if (!field.isPrimaryKey()) {
                fields.add(format("%s=?", field.getSqlName()));
            }
        }
        query.append(fields.toString());
        query.append(format(" WHERE %s=?", table.getPrimaryKey().get().getSqlName()));
        return query.toString();
    }

    protected String buildDeleteQuery(Table<?> table) throws OrmException {
        return format("DELETE FROM %s WHERE %s=?", fullTableName(table), table.getPrimaryKey().get().getSqlName());
    }

    protected String buildSelectQuery(Query root) throws OrmException {
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

    private String expandLinkTables(TableSpec left, Link right) {
        StringBuilder query = new StringBuilder();
        query.append(format(" JOIN %s ON %s.%s=%s.%s ",
                fullTableName(right.getTable()),
                fullTableName(left.getTable()), right.getLeftField().getSqlName(),
                fullTableName(right.getTable()), right.getField().getSqlName()));
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
        StringBuilder query = new StringBuilder();
        query.append(format("%s.%s %s (", fullTableName(table.getTable()), crit.getField().getSqlName(), listOperator(crit)));
        for (Object val : crit.getValues()) {
            query.append(format("'%s'", sqlValue(val)));
        }
        query.append("'");
        return query.toString();
    }

    private String expandValueFieldCriteria(TableSpec table, ValueCriteria crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s.%s%s'%s'", fullTableName(table.getTable()), crit.getField().getSqlName(), valueOperator(crit), sqlValue(crit.getValue())
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
    private String expandOrder(TableSpec table, Order order) {
        StringBuilder query = new StringBuilder();
        query.append(format("%s.%s", fullTableName(table.getTable()), order.getField().getSqlName()));
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
        O pojo = (O) pops.newPojoInstance(table);
        for (Field field : table.getFields()) {
            pops.setValue(pojo, field, getValueFromResultSet(rs, field));
        }
        return pojo;
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
                    stmt.setString(par, getStringFromPojo(pojo, field));
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
    private Object getKeyValueFromResultSet(ResultSet rs, Field field) throws OrmException {
        try {
            switch (field.getFieldType()) {
                case LONG:
                    return rs.getLong(1);
                case INTEGER:
                    return rs.getInt(1);
                case STRING:
                    return rs.getString(1);
                case SHORT:
                case BYTE:
                case DOUBLE:
                case FLOAT:
                case BOOLEAN:
                case ENUM:
                case DATE:
                case TIMESTAMP:
                case DURATION:
                    throw new OrmException(format("Field type '%s' is not a supported primary key type", field.getFieldType()));
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

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
     * Create a comparator based on the tail of the query that will compare
     * Pojos on their fields.
     *
     * @param <O>
     * @param <P>
     * @param tail
     * @return
     */
    private <O, P extends Part> Comparator<Wrapper<O>> makeComparatorForTail(P tail) {
        List<OrderedPart> order = new LinkedList();
        while (tail.getType() == Part.Type.ORDER) {
            order.add((OrderedPart) tail);
            tail = (P) tail.left();
        }
        List<Comparator<Wrapper<O>>> comps = new LinkedList();
        for (OrderedPart op : order) {
            comps.add((Comparator<Wrapper<O>>) (Wrapper<O> w1, Wrapper<O> w2) -> {
                if (op.getDirection() == OrderedPart.Direction.ASCENDING) {
                    return w1.compareTo(w2);
                } else {
                    return w2.compareTo(w1);

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
     */
    private String fullTableName(Table table) {
        return format("%s.%s", databaseName(table), tableName(table));
    }

    /**
     * Work out the short table name to use.
     *
     * @param table The table we're referencing
     * @return The SQL table name
     */
    private String tableName(Table table) {
        return format("%s", table.getSqlTable());
    }

    /**
     * Work out the database name to use.
     *
     * @param table The table we're referencing
     * @return The SQL table name
     */
    private String databaseName(Table table) {
        Database database = table.getDatabase();
        Database alias = aliases.get(database);
        if (alias == null) {
            alias = database;
        }
        return format("%s", alias.getSqlDatabase());
    }

}

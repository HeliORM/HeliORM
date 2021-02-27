package com.heliorm.sql;

import com.heliorm.*;
import com.heliorm.def.Executable;
import com.heliorm.def.Field;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.OrderedPart;
import com.heliorm.impl.Part;
import com.heliorm.impl.SelectPart;
import com.heliorm.query.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

/**
 * @author gideon
 */
public abstract class SqlDriver implements OrmTransactionDriver {

    private static final String POJO_NAME_FIELD = "pojo_field_name";
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

    public final <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
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

    private boolean useUnionAll() {
        return useUnionAll && supportsUnionAll();
    }

    private String buildSelectUnionQuery(List<List<Part>> queries) throws OrmException {
        Set<Field> allFields = queries.stream()
                .map(parts -> parts.get(0).getReturnTable())
                .flatMap(table -> (Stream<Field>) (table.getFields().stream()))
                .collect(Collectors.toSet());
        StringJoiner buf = new StringJoiner(" UNION ALL ");
        Query root = null;
        for (List<Part> parts : queries) {
            root = Parser.parse(parts);
            StringBuilder tablesQuery = new StringBuilder();
            StringJoiner fieldsQuery = new StringJoiner(",");
            List<Field> tableFields = root.getTable().getFields();
            for (Field field : allFields) {
                String fieldId = getFieldId(field);
                if (tableFields.contains(field)) {
                    fieldsQuery.add(format("%s AS %s", fullFieldName(root.getTable(), field), virtualFieldName(fieldId)));
                } else {
                    String empty = "NULL";
                    fieldsQuery.add(format("%s AS %s", empty, virtualFieldName(fieldId)));
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
                                return resultSetHelper.makePojoFromResultSet(rs, tables.get(rs.getString(POJO_NAME_FIELD)));
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

    protected void setEnum(PreparedStatement stmt, int par, String value) throws SQLException {
        stmt.setString(par, value);
    }

    protected abstract boolean supportsUnionAll();


    private String buildSelectQuery(Query root) throws OrmException {
        StringBuilder tablesQuery = new StringBuilder();
        tablesQuery.append("SELECT DISTINCT  ");
        StringJoiner fieldList = new StringJoiner(",");
        for (Field field : root.getTable().getFields()) {
            fieldList.add(format("%s AS %s", fullFieldName(root.getTable(), field), virtualFieldName(getFieldId(field))));
        }
        tablesQuery.append(fieldList.toString());
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
            case IS_FIELD:
                return expandIsFieldCriteria(table, (IsCriteria) crit);
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

    private String expandIsFieldCriteria(TableSpec table, IsCriteria crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s%s", fullFieldName(table.getTable(), crit.getField()), isOperator(crit)));
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
        if (order.getDirection() == Order.Direction.DESCENDING) {
            query.append(" DESC");
        }
        if (order.getThenBy().isPresent()) {
            query.append(", ");
            query.append(expandOrder(table, order.getThenBy().get()));
        }
        return query.toString();
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

    private String isOperator(IsCriteria part) throws OrmException {
        switch (part.getOperator()) {
            case IS_NULL:
                return " IS NULL";
            case IS_NOT_NULL:
                return " IS NOT NULL";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", part.getOperator()));
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

    /** Expand the query parts in the list so that the query branches into
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

    private String getFieldId(Field field) {
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
            close(con);
        } catch (Exception ex) {
            error = error != null ? error : ex;
        }
        if (error != null) {
            throw new UncaughtOrmException(error.getMessage(), error);
        }
    }

}

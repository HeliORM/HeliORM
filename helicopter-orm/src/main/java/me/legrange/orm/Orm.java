package me.legrange.orm;

import static java.lang.String.format;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.legrange.orm.driver.MySqlOrm;
import me.legrange.orm.impl.JoinPart;
import me.legrange.orm.impl.OrderedPart;
import me.legrange.orm.impl.Part;
import me.legrange.orm.impl.SelectPart;
import me.legrange.orm.rep.Parser;
import me.legrange.orm.rep.Query;

/**
 * @author gideon
 */
public abstract class Orm implements AutoCloseable {

    public enum Driver {
        MYSQL;
    }

    private final Connection connection;
    private final PojoOperations pops;
    private final Map<Class<?>, Table> tables = new HashMap();
    private final Map<Table, PreparedStatement> inserts = new HashMap();
    private final Map<Table, PreparedStatement> updates = new HashMap();
    private final Map<Table, PreparedStatement> deletes = new HashMap();

    public static Orm open(Connection con, Driver driver) throws OrmException {
        switch (driver) {
            case MYSQL:
                return new MySqlOrm(con);
            default:
                throw new OrmException(format("Unsupported database type %s. BUG!", driver));
        }
    }

    public Orm(Connection con) throws OrmException {
        this.connection = con;
        pops = new UnsafeFieldOperation();
    }

    public <O> O create(O pojo) throws OrmException {
        return create(tableFor(pojo), pojo);
    }

    private <T extends Table<O>, O> O create(T table, O pojo) throws OrmException {
        try {
            PreparedStatement stmt = inserts.get(table);
            if (stmt == null) {
                stmt = getConnection().prepareStatement(buildInsertQuery(table), Statement.RETURN_GENERATED_KEYS);
                inserts.put(table, stmt);
            }
            int par = 1;
            for (Field field : table.getFields()) {
                setValueInStatement(stmt, pojo, field, par);
                par++;
            }
            stmt.executeUpdate();
            Optional<Field> opt = table.getPrimaryKey();
            if (opt.isPresent()) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pops.setValue(pojo, opt.get(), getKeyValueFromResultSet(rs, opt.get()));
                    }
                }
            }
            return pojo;
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    public <O> O update(O pojo) throws OrmException {
        return update(tableFor(pojo), pojo);
    }

    private <T extends Table<O>, O> O update(T table, O pojo) throws OrmException {
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
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    public <O> void delete(O pojo) throws OrmException {
        delete(tableFor(pojo), pojo);
    }

    private <T extends Table<O>, O> void delete(T table, O pojo) throws OrmException {
        try {
            PreparedStatement stmt = deletes.get(table);
            if (stmt == null) {
                stmt = getConnection().prepareStatement(buildDeleteQuery(table));
                deletes.put(table, stmt);
            }
            setValueInStatement(stmt, pojo, table.getPrimaryKey().get(), 1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart(null, table, this);
    }

    @Override
    public void close() throws OrmException {
    }

    public <O> Table<O> tableFor(O pojo) throws OrmException {
        if (tables.isEmpty()) {
            ServiceLoader<Table> svl = ServiceLoader.load(Table.class);
            for (Table table : svl) {
                tables.put(table.getObjectClass(), table);
            }
        }
        Table<O> table = tables.get(pojo.getClass());
        if (table == null) {
            throw new OrmException("Cannot find table for pojo of type " + pojo.getClass().getCanonicalName());
        }
        return table;
    }

    private Connection getConnection() {
        return connection;
    }

    /**
     * Execute the supplied programmed query and return a list of result pojos.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The list of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
        return ((Stream<O>) stream(tail)).collect(Collectors.toList());
    }

    /**
     * Execute the supplied programmed query and return a stream of result
     * pojos.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The stream of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException {
        List<List<Part>> queries = explodeAbstractions(tailToList(tail));
        if (queries.isEmpty()) {
            throw new OrmException("Could not build query from parts. BUG!");
        }
        Stream<O> res = null;
        for (List<Part> parts : queries) {
            Stream<O> stream = streamSingle(parts.get(0).getReturnTable(), buildSelectQuery(Parser.parse(parts)));
            if (res == null) {
                res = stream;
            } else {
                res = Stream.concat(res, stream);
            }
        }
        if ((queries.size() > 1) && (tail.getType() == Part.Type.ORDER)) {
            res = res.sorted(makeComparatorForTail(tail));
        }
        return res;
    }

    private <O, P extends Part & Executable> Stream<O> streamSingle(Table<O> table, String query) throws OrmException {
        Connection sql = getConnection();
        List<O> result = new ArrayList();
        try {
            Statement stmt = sql.createStatement();
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
                                return makePojoFromResultSet(rs, table);
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
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    /**
     * Execute the supplied programmed query and return an optional with a
     * possible result. It is expected that either zero or one results will be
     * found, so more than one result will cause an error.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The optional found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> Optional<O> oneOrNone(P tail) throws OrmException {
        Stream<O> stream = stream(tail);
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

    /**
     * Execute the supplied programmed query and return exactly one matching
     * result. It is expected that exactly one result will be found, so no
     * result or more than one result will cause an error.
     *
     * @param <O> The type of Pojo to return
     * @param <P> The type of programmed query
     * @param tail The tail of the query
     * @return The found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     * go wrong did go wrong.
     */
    public <O, P extends Part & Executable> O one(P tail) throws OrmException {
        Stream<O> stream = stream(tail);
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

    /**
     * Build a SQL select query for the given table.
     *
     * @param query The query structure
     * @return The SQL query text
     * @throws OrmException Thrown if there is an error building the query.
     */
    protected abstract String buildSelectQuery(Query query) throws OrmException;

    /**
     * Build a SQL insert query based on the supplied query structure.
     *
     * @param table The table we're inserting into
     * @return The SQL query text
     * @throws OrmException Thrown if there is an error building the query.
     */
    protected abstract String buildInsertQuery(Table<?> table) throws OrmException;

    /**
     * Build a SQL update query for the given table.
     *
     * @param table The table we're updating in
     * @return The SQL query text
     * @throws OrmException Thrown if there is an error building the query.
     */
    protected abstract String buildUpdateQuery(Table<?> table) throws OrmException;

    /**
     * Build a SQL delete query for the given table.
     *
     * @param table The table we're updating in
     * @return The SQL query text
     * @throws OrmException Thrown if there is an error building the query.
     */
    protected abstract String buildDeleteQuery(Table<?> table) throws OrmException;

    /**
     * Creates a Pojo for the given table from the element currently at the
     * result set cursor.
     *
     * @param <O> The type of Pojo
     * @param rs The result set
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
     * Extract the value for the given field from a SQL result set.
     *
     * @param rs The result set
     * @param field The field for which we're reading data
     * @return The data
     * @throws OrmException Thrown if we cannot work out how to extract the
     * data.
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
                case ENUM:
                    Class javaType = field.getJavaType();
                    if (!javaType.isEnum()) {
                        throw new OrmException(format("Field %s is not an enum. BUG!", field.getJavaName()));
                    }
                    return Enum.valueOf(javaType, rs.getString(column));
                case STRING:
                    return rs.getString(column);
                case DATE:
                    return rs.getDate(column);
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    /**
     * Retrieve the returned key value from a result set (used for updating
     * auto-increment keys).
     *
     * @param rs The result set
     * @param field The field for which we're reading data
     * @return The data
     * @throws OrmException Thrown if we cannot work out how to extract the
     * data.
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
                    throw new OrmException(format("Field type '%s' is not a supported primary key type", field.getFieldType()));
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

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
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    private Object getValueFromPojo(Object pojo, Field field) throws OrmException {
        return pops.getValue(pojo, field);
    }

    private String getStringFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (!(value instanceof String)) {
            throw new OrmException(format("Could not read String value for field type '%s'.", field.getFieldType()));
        }
        return (String) value;
    }

    private java.sql.Date getDateFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (!(value instanceof java.util.Date)) {
            throw new OrmException(format("Could not read Date value for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
        }
        return new java.sql.Date(((java.util.Date) value).getTime());
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

    /**
     * Create a comparator based on the tail of the query that will compare
     * Pojos on their fields.
     *
     * @param <O>
     * @param <P>
     * @param tail
     * @return
     */
    private <O, P extends Part> Comparator<O> makeComparatorForTail(P tail) {
        List<OrderedPart> order = new LinkedList();
        while (tail.getType() == Part.Type.ORDER) {
            order.add((OrderedPart) tail);
            tail = (P) tail.left();
        }
        List<Comparator<O>> comps = new LinkedList();
        for (OrderedPart op : order) {
            comps.add((Comparator) (Object o1, Object o2) -> {
                try {
                    if (op.getDirection() == OrderedPart.Direction.ASCENDING) {
                        return pops.compareTo(o1, o2, op.getField());
                    } else {
                        return -pops.compareTo(o1, o2, op.getField());
                    }
                } catch (OrmException ex) {
                    throw new UncaughtOrmException(ex.getMessage(), ex);
                }
            });
        }
        return new CompoundComparator(comps);
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
     * @param idx the index of the part being examined
     * @return The expanded query parts lists.
     */
    private List<List<Part>> explode(List<Part> parts, int idx) {
        Part part = parts.get(idx);
        List<List<Part>> res = new LinkedList();
        if ((part.getType() == Part.Type.SELECT) || (part.getType() == Part.Type.JOIN)) {
            Table<?> table = part.getSelectTable();
            Set<Table> subTables = table.getSubTables();
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
}

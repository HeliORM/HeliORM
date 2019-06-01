package me.legrange.orm;

import static java.lang.String.format;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.legrange.orm.driver.MySqlOrm;
import me.legrange.orm.impl.Part;
import me.legrange.orm.impl.SelectPart;
import me.legrange.orm.rep.Parser;
import me.legrange.orm.rep.Query;

/**
 *
 * @author gideon
 */
public abstract class Orm implements AutoCloseable {

    public enum Driver {
        MYSQL;
    }

    private final Connection connection;
    private final PojoOperations pops;
    private final Map<Table, PreparedStatement> inserts = new HashMap();

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

    public <T extends Table<O>, O> O create(T table, O pojo) throws OrmException {
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
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                // TODO: get the auto-generated key from the result set and apply it to the Pojo.
                // this requires me to know the key field
            }
            return pojo;
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    public <T extends Table<O>, O> O update(T table, O pojo) throws OrmException {
        return pojo;
    }

    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart(null, table, this);
    }

    @Override
    public void close() throws OrmException {
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
        String query = buildSelectQuery(Parser.parse(tailToList(tail)));
        Connection sql = getConnection();
        List<O> result = new ArrayList();
        Table<O> table = tail.getReturnTable();
        try (Statement stmt = sql.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.add(makePojoFromResultSet(rs, table));
            }
            return result;
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
        //return ((Stream<O>) stream(tail)).collect(Collectors.toList());
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
        String query = buildSelectQuery(Parser.parse(tailToList(tail)));
        Connection sql = getConnection();
        List<O> result = new ArrayList();
        try {
            Statement stmt = sql.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            Table<O> table = tail.getReturnTable();
            return StreamSupport.stream(
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
        } catch (SQLException ex) {
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
        Optional<O> first = stream.findFirst();
        if (first.isEmpty()) {
            return first;
        }
        stream.skip(1);
        if (stream.findFirst().isPresent()) {
            throw new OrmException(format("Required one or none %s but found more than one", tail.getReturnTable().getObjectClass().getSimpleName()));
        }
        return first;
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
        Optional<O> first = stream.findFirst();
        if (first.isEmpty()) {
            throw new OrmException(format("Required exactly one %s but found none", tail.getReturnTable().getObjectClass().getSimpleName()));
        }
        stream.skip(1);
        if (stream.findFirst().isPresent()) {
            throw new OrmException(format("Required exactly one %s but found more than one", tail.getReturnTable().getObjectClass().getSimpleName()));
        }
        return first.get();
    }

    /**
     * Build a SQL select query based on the supplied query structure.
     *
     * @param query The query structure
     * @return The SQL query text
     * @throws OrmException Thrown if there is an error building the query.
     */
    protected abstract String buildSelectQuery(Query query) throws OrmException;

    /**
     * Build a SQL insert query based on the supplied query structure.
     *
     * @param table The
     * @return The SQL query text
     * @throws OrmException Thrown if there is an error building the query.
     */
    protected abstract String buildInsertQuery(Table<?> table) throws OrmException;

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

}

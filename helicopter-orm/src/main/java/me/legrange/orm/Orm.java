package me.legrange.orm;

import static java.lang.String.format;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

    private final Connection con;
    private final PojoOperations pops;

    public static Orm open(Connection con, Driver driver) throws OrmException {
        switch (driver) {
            case MYSQL:
                return new MySqlOrm(con);
            default:
                throw new OrmException(format("Unsupported database type %s. BUG!", driver));
        }
    }

    public Orm(Connection con) throws OrmException {
        this.con = con;
        pops = new UnsafeFieldOperation();
    }

    public <T> T create(T data) throws OrmException {
        return data;
    }

    public <T> T udate(T data) throws OrmException {
        return data;
    }

    public <T extends Table<O>, O> Select<T, O, T, O> select(T table) {
        return new SelectPart(null, table, this);
    }

    @Override
    public void close() throws OrmException {
    }

    public Connection getCon() {
        return con;
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
        String query = buildQuery(Parser.parse(tailToList(tail)));
        Connection sql = getCon();
        List<O> result = new ArrayList();
        try (Statement stmt = sql.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            Table<O> table = tail.getReturnTable();
            while (rs.next()) {
                O pojo = (O) pops.newPojoInstance(table);
                for (Field field : table.getFields()) {
                    pops.setValue(pojo, field, getValueFromResultSet(rs, field));
                }
                result.add(pojo);
            }
            return result;
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    /**
     * Build a SQL query based on the supplied query structure.
     *
     * @param query The quert structure
     * @return The SQL query text
     * @throws OrmException Thrown if there is an error building the query.
     */
    protected abstract String buildQuery(Query query) throws OrmException;

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

package me.legrange.orm;

import static java.lang.String.format;
import java.sql.Connection;
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

    public static Orm open(Connection con, Driver driver) throws OrmException {
        switch (driver) {
            case MYSQL:
                return new MySqlOrm(con);
            default:
                throw new OrmException(format("Unsupported database type %s. BUG!", driver));
        }
    }

    public Orm(Connection con) {
        this.con = con;
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

    public <O, P extends Part & Executable> List<O> list(P tail) throws OrmException {
//        try {

        List<Part> parts = new ArrayList();
        Part part = tail.head();
        while (part != null) {
            parts.add(part);
            part = part.right();
        }
        Parser parser = new Parser(parts);
        String q = buildQuery(parser.parse());
        System.out.println("Q: " + q);
        // Node.unroll(tail).dump(0);
//        List<Part> parts = unroll(tail);
//        String query = buildQuery(Node.unroll(tail));
//        Connection sql = getCon();
        List<O> result = new ArrayList();
//        System.out.println(query);
//            try (Statement stmt = sql.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//                Table<O> table = ((SelectPart) parts.get(0)).getReturnTable();
//                while (rs.next()) {
//                    try {
//                        O obj = table.getObjectClass().newInstance();
//                    } catch (InstantiationException | IllegalAccessException ex) {
//                        throw new OrmException(ex.getMessage(), ex);
//                    }
//                    for (Field field : table.getFields()) {
//
//                    }
//                }
//            }
        return result;
//        } catch (SQLException ex) {
//            throw new OrmException(ex.getMessage(), ex);
//        }
    }

    protected abstract String buildQuery(Query query) throws OrmException;

}

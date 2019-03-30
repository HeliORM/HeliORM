package me.legrange.orm;

import java.sql.Connection;

/**
 *
 * @author gideon
 */
public class Orm implements AutoCloseable {

    public enum Dialect {
        MYSQL;
    }

    private final Connection con;
    private final Dialect dialect;

    public static Orm open(Connection con, Dialect dialect) {
        return new Orm(con, dialect);
    }

    public Orm(Connection con, Dialect dialect) {
        this.con = con;
        this.dialect = dialect;
    }

    public <T> T create(T data) throws OrmException {
        return data;
    }

    public <T> T udate(T data) throws OrmException {
        return data;
    }

    public <T extends Table<O>, O> Select<T, O> select(T table) {
        return null;
    }

    @Override
    public void close() throws OrmException {
    }

}

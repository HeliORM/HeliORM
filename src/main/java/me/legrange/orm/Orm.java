package me.legrange.orm;

import java.sql.Connection;
import me.legrange.orm.impl.SelectPart;

/**
 *
 * @author gideon
 */
public class Orm implements AutoCloseable {

    private final Connection con;

    public static Orm open(Connection con) {
        return new Orm(con);
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

}

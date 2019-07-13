package net.legrange.orm;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * A builder pattern that allows for very specific ORM configuration.
 *
 * @author gideon
 */
public class OrmBuilder {

    private final Connection con;
    private final Map<Database, String> databases = new HashMap();
    private Orm.Dialect dialect;

    private OrmBuilder(Connection con) {
        this.con = con;
    }

    public OrmBuilder mapDatabase(Database database, String sqlName) {
        databases.put(database, sqlName);
        return this;
    }

    public OrmBuilder setDialect(Orm.Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public Orm build() throws OrmException {
        Orm orm = Orm.open(con, dialect);
//        for (Database database : databases) {
//            orm.mapDatabase(database, databases.get(database));
//        }
        return orm;
    }

    public static OrmBuilder create(Connection con) {
        return new OrmBuilder(con);
    }

}

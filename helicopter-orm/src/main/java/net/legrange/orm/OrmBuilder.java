package net.legrange.orm;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gideon
 */
public class OrmBuilder {

    private final Connection con;
    private Map<Database, String> databases = new HashMap();
    private Orm.Dialect dialect;

    private OrmBuilder(Connection con) {
        this.con = con;
    }

    public OrmBuilder withDatabase(Database database, String sqlName) {
        databases.put(database, sqlName);
        return this;
    }

    public OrmBuilder withDialect(Orm.Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public Orm build() throws OrmException {
        return Orm.open(con, dialect);
    }

    public static OrmBuilder create(Connection con) {
        return new OrmBuilder(con);
    }

}

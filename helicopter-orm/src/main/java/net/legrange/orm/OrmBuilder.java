package net.legrange.orm;

import static java.lang.String.format;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.legrange.orm.driver.MySqlDriver;
import net.legrange.orm.impl.AliasDatabase;

/**
 * A builder pattern that allows for very specific ORM configuration.
 *
 * @author gideon
 */
public class OrmBuilder {

    private final Supplier<Connection> con;
    private final Map<Database, String> databases = new HashMap();
    private Orm.Dialect dialect;
    private PojoOperations pops;

    private OrmBuilder(Supplier<Connection> con) throws OrmException {
        this.con = con;
        this.dialect = Orm.Dialect.MYSQL;
        this.pops = new UnsafeFieldOperation();
    }

    public OrmBuilder mapDatabase(Database database, String sqlName) {
        databases.put(database, sqlName);
        return this;
    }

    public OrmBuilder setDialect(Orm.Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public OrmBuilder withPojoOperations(PojoOperations pops) {
        this.pops = pops;
        return this;
    }

    public Orm build() throws OrmException {
        Map<Database, Database> aliases = new HashMap();
        for (Database database : databases.keySet()) {
            aliases.put(database, new AliasDatabase(database, databases.get(database)));
        }
        OrmDriver driver;
        switch (dialect) {
            case MYSQL:
                driver = new MySqlDriver(con, pops, aliases);
                break;
            default:
                throw new OrmException(format("Don't know how to create a driver for dialect %s", dialect));
        }
        return new Orm(driver);
    }

    public static OrmBuilder create(Supplier<Connection> con) throws OrmException {
        return new OrmBuilder(con);
    }

    public static OrmBuilder create(Connection con) throws OrmException {
        return new OrmBuilder(() -> con);
    }

}

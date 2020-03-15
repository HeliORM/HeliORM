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
    private boolean rollbackOnUncommittedClose = false;

    /**
     * Create a new OrmBuilder using the given connection supplier. This makes
     * it easy to use a connection pool, since most pool implementation has a
     * getConnection() method, so one can just pass () -> pool.getConnection()
     * to create.
     *
     * @param con The connection supplier
     * @return The ORM builder
     * @throws OrmException
     */
    public static OrmBuilder create(Supplier<Connection> con) throws OrmException {
        return new OrmBuilder(con);
    }

    /**
     * Create a new OrmBuilder using the given connection. T
     *
     * @param con The connection to use
     * @return The ORM builder
     * @throws OrmException
     */
    public static OrmBuilder create(Connection con) throws OrmException {
        return new OrmBuilder(() -> con);
    }
    /**
     * Create a new ORM builder.
     *
     * @param con The connection supplier to start with.
     * @throws OrmException
     */
    private OrmBuilder(Supplier<Connection> con) throws OrmException {
        this.con = con;
        this.dialect = Orm.Dialect.MYSQL;
        this.pops = new UnsafePojoOperations();
    }

    /**
     * Map a database object to a specific SQL database.
     *
     * @param database The database
     * @param sqlName The SQL database
     * @returnThe ORM builder
     */
    public OrmBuilder mapDatabase(Database database, String sqlName) {
        databases.put(database, sqlName);
        return this;
    }

    /**
     * Select the SQL dialect to use.
     *
     * @param dialect The dialect
     * @return The ORM builder
     */
    public OrmBuilder setDialect(Orm.Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    /**
     * Select the PojoOperations utility to use for serializing and
     * deserializing POJOs to and from SQL data.
     *
     * @param pops PojoOperations implementation
     * @return The ORM builder
     */
    public OrmBuilder withPojoOperations(PojoOperations pops) {
        this.pops = pops;
        return this;
    }

    /** Setup the ORM to rollback transactions if a transaction is auto-closed at the end of a try-with-resources block.
     * Default is false and this means that by default an exception will be thrown if there is no commit or rollback call
     * before the block closes.
     *
     * @param rollback if rollback is preferred to an exception
     * @return * @return The ORM builder
     */
    public OrmBuilder setRollbackOnUncommittedClose(boolean rollback) {
        this.rollbackOnUncommittedClose = rollback;
        return this;
    }


    /**
     * Create the ORM based on the setup supplied using the builder pattern.
     *
     * @return The ORM instance
     * @throws OrmException
     */
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
        if (driver instanceof OrmTransactionDriver) {
            ((OrmTransactionDriver)driver).setRollbackOnUncommittedClose(rollbackOnUncommittedClose);
        }
        return new Orm(driver);
    }

}

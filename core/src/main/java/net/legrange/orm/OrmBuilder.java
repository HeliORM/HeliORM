package net.legrange.orm;

import net.legrange.orm.driver.SqlDriver;
import net.legrange.orm.impl.AliasDatabase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * A builder pattern that allows for very specific ORM configuration.
 *
 * @author gideon
 */
public class OrmBuilder {

    private final Supplier<Connection> con;
    private final Class<? extends SqlDriver> driverClass;
    private final Map<Database, String> databases = new HashMap();
    private PojoOperations pops;
    private boolean rollbackOnUncommittedClose = false;
    private boolean createMissingTables = false;

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
    public static OrmBuilder create(Supplier<Connection> con, Class<? extends SqlDriver> driverClass) throws OrmException {
        return new OrmBuilder(con, driverClass);
    }

    /**
     * Create a new ORM builder.
     *
     * @param con The connection supplier to start with.
     * @throws OrmException
     */
    private OrmBuilder(Supplier<Connection> con, Class<? extends SqlDriver> driverClass) throws OrmException {
        this.con = con;
        this.driverClass = driverClass;
        this.pops = new UnsafePojoOperations();
    }

    /**
     * Map a database object to a specific SQL database.
     *
     * @param database The database
     * @param sqlName  The SQL database
     * @returnThe ORM builder
     */
    public OrmBuilder mapDatabase(Database database, String sqlName) {
        databases.put(database, sqlName);
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

    /**
     * Setup the ORM to rollback transactions if a transaction is auto-closed at the end of a try-with-resources block.
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
    
    public OrmBuilder setCreateMissingTables(boolean createMissingTables) { 
        this.createMissingTables = createMissingTables;
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
        SqlDriver driver;
        try {
            Constructor<? extends  SqlDriver> constructor = driverClass.getConstructor(Supplier.class, PojoOperations.class, Map.class);
            driver = constructor.newInstance(con, pops, aliases);
            driver.setCreateTables(createMissingTables);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new OrmException(format("Cannot start driver of type '%s' (%s)", driverClass.getSimpleName(), e.getMessage()),e);
        }
        if (driver instanceof OrmTransactionDriver) {
            ((OrmTransactionDriver) driver).setRollbackOnUncommittedClose(rollbackOnUncommittedClose);
        }
        return new Orm(driver);
    }

}

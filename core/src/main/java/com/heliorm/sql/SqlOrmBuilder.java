package com.heliorm.sql;

import com.heliorm.Orm;
import com.heliorm.OrmException;
import com.heliorm.impl.AliasDatabase;
import com.heliorm.Database;
import com.heliorm.OrmTransactionDriver;

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
public class SqlOrmBuilder {

    private final Supplier<Connection> con;
    private final Class<? extends SqlDriver> driverClass;
    private final Map<Database, String> databases = new HashMap();
    private PojoOperations pops;
    private boolean rollbackOnUncommittedClose = false;
    private boolean createMissingTables = false;
    private boolean useUnionAll = true;

    /**
     * Create a new OrmBuilder using the given connection supplier. This makes
     * it easy to use a connection pool, since most pool implementation has a
     * getConnection() method, so one can just pass a lambda.
     * to create.
     *
     * @param con The connection supplier
     * @return The ORM builder
     * @throws OrmException Thrown if there is a problem creating the OrmBuilder
     */
    public static SqlOrmBuilder create(Supplier<Connection> con, Class<? extends SqlDriver> driverClass) throws OrmException {
        return new SqlOrmBuilder(con, driverClass);
    }

    /**
     * Create a new ORM builder.
     *
     * @param con The connection supplier to start with.
     * @throws OrmException Thrown if there is a problem creating the OrmBuilder
     */
    private SqlOrmBuilder(Supplier<Connection> con, Class<? extends SqlDriver> driverClass) throws OrmException {
        this.con = con;
        this.driverClass = driverClass;
        this.pops = new UnsafePojoOperations();
    }

    /**
     * Map a database object to a specific SQL database.
     *
     * @param database The database
     * @param sqlName  The SQL database
     */
    public SqlOrmBuilder mapDatabase(Database database, String sqlName) {
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
    public SqlOrmBuilder withPojoOperations(PojoOperations pops) {
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
    public SqlOrmBuilder setRollbackOnUncommittedClose(boolean rollback) {
        this.rollbackOnUncommittedClose = rollback;
        return this;
    }
    
    public SqlOrmBuilder setCreateMissingTables(boolean createMissingTables) {
        this.createMissingTables = createMissingTables;
        return this;
    }

    /** Configure the ORM to use or not use SQU 'UNION ALL' statements.
     * Default is true
     *
     * @param useUnionAll Use or don't use 'UNION ALL'
     * @return The OrmBuilder
     */
    public SqlOrmBuilder setUseUnionAll(boolean useUnionAll) {
        this.useUnionAll = useUnionAll;
        return this;
    }

    /**
     * Create the ORM based on the setup supplied using the builder pattern.
     *
     * @return The ORM instance
     * @throws OrmException Thrown if there is an error building the ORM
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
            driver.setUseUnionAll(useUnionAll);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new OrmException(format("Cannot start driver of type '%s' (%s)", driverClass.getSimpleName(), e.getMessage()),e);
        }
        if (driver instanceof OrmTransactionDriver) {
            ((OrmTransactionDriver) driver).setRollbackOnUncommittedClose(rollbackOnUncommittedClose);
        }
        return new SqlOrm(driver);
    }

}

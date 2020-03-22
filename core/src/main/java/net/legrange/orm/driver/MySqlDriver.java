package net.legrange.orm.driver;

import net.legrange.orm.Database;
import net.legrange.orm.PojoOperations;
import net.legrange.orm.Table;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * @author gideon
 */
public final class MySqlDriver extends SqlDriver {

    public MySqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops) {
        super(connectionSupplier, pops);
    }

    public MySqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops, Map<Database, Database> aliases) {
        super(connectionSupplier, pops, aliases);
    }

    @Override
    protected String fullTableName(Table table) {
        return format("%s.%s", databaseName(table), tableName(table));
    }
}

package net.legrange.orm.driver;

import net.legrange.orm.Database;
import net.legrange.orm.PojoOperations;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;

public class PostgreSqlDriver extends SqlDriver {

    public PostgreSqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops) {
        super(connectionSupplier, pops);
    }

    public PostgreSqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops, Map<Database, Database> aliases) {
        super(connectionSupplier, pops, aliases);
    }
}

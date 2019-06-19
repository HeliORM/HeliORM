package net.legrange.orm.driver;

import java.sql.Connection;
import java.util.function.Supplier;
import net.legrange.orm.PojoOperations;

/**
 *
 * @author gideon
 */
public final class MySqlDriver extends SqlDriver {

    public MySqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops) {
        super(connectionSupplier, pops);
    }

}

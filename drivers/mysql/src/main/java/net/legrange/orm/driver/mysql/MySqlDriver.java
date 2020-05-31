package net.legrange.orm.driver.mysql;

import net.legrange.orm.Database;
import net.legrange.orm.OrmException;
import net.legrange.orm.PojoOperations;
import net.legrange.orm.Table;
import net.legrange.orm.def.Field;
import net.legrange.orm.driver.SqlDriver;
import net.legrange.orm.driver.TableGenerator;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;

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
    protected String fullTableName(Table table) throws OrmException {
        return String.format("%s.%s", databaseName(table), tableName(table));
    }

    @Override
    protected String fullFieldName(Table table, Field field) throws OrmException {
        return String.format("%s.`%s`", fullTableName(table), field.getSqlName());
    }

    @Override
    protected String fieldName(Table table, Field field) throws OrmException {
        return fullFieldName(table, field);
    }

    @Override
    protected TableGenerator getTableGenerator() throws OrmException {
        return new MysqlDialectGenerator();
    }
}

package net.legrange.orm.driver;

import net.legrange.orm.Database;
import net.legrange.orm.PojoOperations;
import net.legrange.orm.Table;
import net.legrange.orm.def.Field;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;

public final class PostgreSqlDriver extends SqlDriver {

    public PostgreSqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops) {
        super(connectionSupplier, pops);
    }

    public PostgreSqlDriver(Supplier<Connection> connectionSupplier, PojoOperations pops, Map<Database, Database> aliases) {
        super(connectionSupplier, pops, aliases);
    }

    @Override
    protected String fullTableName(Table table) {
        return format("\"%s\".public.\"%s\"", databaseName(table), tableName(table));
    }

    @Override
    protected String fullFieldName(Table table, Field field) {
        return format("%s.\"%s\"", fullTableName(table), field.getSqlName());
    }

    @Override
    protected String fieldName(Table table, Field field) {
        return format("\"%s\"", field.getSqlName());
    }

    @Override
    protected void setEnum(PreparedStatement stmt, int par, String value) throws SQLException {
        stmt.setObject(par, value, Types.OTHER);
    }
}

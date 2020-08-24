package com.heliorm.driver.postgresql;

import com.heliorm.Database;
import com.heliorm.OrmException;
import com.heliorm.PojoOperations;
import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.driver.OrmSqlException;
import com.heliorm.driver.SqlDriver;
import com.heliorm.driver.TableGenerator;

import java.sql.*;
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
    protected String fullTableName(Table table) throws OrmException {
        return format("\"%s\".public.\"%s\"", databaseName(table), tableName(table));
    }

    @Override
    protected String fullFieldName(Table table, Field field) throws OrmException {
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

    @Override
    protected String virtualFieldName(String name) {
        return format("\"%s\"", name);
    }

    @Override
    protected String virtualValue(String name) {
        return format("'%s'", name);
    }

    @Override
    protected TableGenerator getTableGenerator() throws OrmException {
        return new PostgresDialectGenerator();
    }

    protected Object getKeyValueFromResultSet(ResultSet rs, Field field) throws OrmException {
        try {
            switch (field.getFieldType()) {
                case LONG:
                    return rs.getLong(field.getSqlName());
                case INTEGER:
                    return rs.getInt(field.getSqlName());
                case STRING:
                    return rs.getString(field.getSqlName());
                case SHORT:
                case BYTE:
                case DOUBLE:
                case FLOAT:
                case BOOLEAN:
                case ENUM:
                case DATE:
                case TIMESTAMP:
                case DURATION:
                    throw new OrmException(format("Field type '%s' is not a supported primary key type", field.getFieldType()));
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }
}

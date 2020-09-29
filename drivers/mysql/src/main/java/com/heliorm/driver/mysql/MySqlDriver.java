package com.heliorm.driver.mysql;

import com.heliorm.Database;
import com.heliorm.OrmException;
import com.heliorm.PojoOperations;
import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.driver.OrmSqlException;
import com.heliorm.driver.SqlDriver;
import com.heliorm.driver.TableGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    protected String fullTableName(Table table) throws OrmException {
        return format("%s.%s", databaseName(table), tableName(table));
    }

    @Override
    protected String fullFieldName(Table table, Field field) throws OrmException {
        return format("%s.`%s`", fullTableName(table), field.getSqlName());
    }

    @Override
    protected String fieldName(Table table, Field field) throws OrmException {
//        return fullFieldName(table, field);
        return format("`%s`", field.getSqlName());
    }

    @Override
    protected String virtualFieldName(String name) {
        return format("`%s`", name);
    }

    @Override
    protected String virtualValue(String name) {
        return format("'%s'", name);
    }

    @Override
    protected TableGenerator getTableGenerator() throws OrmException {
        return new MysqlDialectGenerator();
    }

    /**
     * Retrieve the returned key value from a result set (used for updating
     * auto-increment keys).
     *
     * @param rs    The result set
     * @param field The field for which we're reading data
     * @return The data
     * @throws OrmException Thrown if we cannot work out how to extract the
     *                      data.
     */
    @Override
    protected Object getKeyValueFromResultSet(ResultSet rs, Field field) throws OrmException {
        try {
            int idx = 1;
            switch (field.getFieldType()) {
                case LONG:
                    return rs.getLong(idx);
                case INTEGER:
                    return rs.getInt(idx);
                case STRING:
                    return rs.getString(idx);
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

    @Override
    protected boolean supportsUnionAll() {
        return false;
    }
}

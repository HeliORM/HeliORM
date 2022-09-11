package com.heliorm.sql.mysql;

import com.heliorm.Database;
import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.sql.OrmSqlException;
import com.heliorm.sql.SqlDriver;
import com.heliorm.sql.TableGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

import static java.lang.String.format;

/**
 * @author gideon
 */
public final class MySqlDriver extends SqlDriver {

    public MySqlDriver(Map<Database, Database> aliases) {
        super(aliases);
    }

    public MySqlDriver() {
        super(Collections.emptyMap());
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
    protected String fieldName(Table table, Field field) {
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
    protected TableGenerator getTableGenerator() {
        return new MysqlDialectGenerator();
    }

    @Override
    protected String castNull(Field field) {
        return "NULL";
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
                case INSTANT:
                    throw new OrmException(format("Field type '%s' is not a supported primary key type", field.getFieldType()));
                default:
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

    @Override
    protected String fieldType(Table<?> table, Field field) throws OrmException {
        switch (field.getFieldType()) {
            case BOOLEAN:
                return "TINYINT(1)";
            case BYTE:
                return "TINYINT";
            case SHORT:
                return "SMALLINT";
            case INTEGER:
                return "INTEGER";
            case LONG:
                return "BIGINT";
            case DOUBLE:
                return "DOUBLE";
            case FLOAT:
                return "REAL";
            case ENUM:
                return format("ENUM(%s)", getEnumValues(field));
            case STRING: {
                int length = 255;
                if (field.isPrimaryKey()) {
                    length = 36;
                }
                if (field.getLength().isPresent()) {
                    length = (int) field.getLength().get();
                }
                return format("VARCHAR(%d)", length);
            }
            case DATE:
                return "DATE";
            case INSTANT:
                return "DATETIME";
            default:
                throw new OrmSqlException(format("Unkown field type '%s'. BUG!", field.getFieldType()));
        }
    }

    private String getEnumValues(Field<?, ?> field) {
        StringJoiner sql = new StringJoiner(",");
        Class<?> javaType = field.getJavaType();
        for (Object v : javaType.getEnumConstants()) {
            sql.add(format("'%s'", ((Enum) v).name()));
        }
        return sql.toString();
    }

    @Override
    protected boolean supportsUnionAll() {
        return true;
    }


    @Override
    protected boolean supportsTransactions() {
        return true;
    }
}

package com.heliorm.sql.postgresql;

import com.heliorm.Database;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.Field;
import com.heliorm.sql.OrmSqlException;
import com.heliorm.sql.SqlDriver;
import com.heliorm.sql.TableGenerator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Map;

import static java.lang.String.format;

public final class PostgreSqlDriver extends SqlDriver {

    public PostgreSqlDriver(Map<Database, Database> aliases) {
        super(aliases);
    }

    public PostgreSqlDriver() {
        super(Collections.EMPTY_MAP);
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

    @Override
    protected String castNull(Field field) throws OrmException {
        return format("CAST(NULL AS %s)", fieldType(field.getTable(), field));
    }

    @Override
    protected String fieldType(Table table, Field field) throws OrmException {
        switch (field.getFieldType()) {
            case BOOLEAN:
                return "BIT";
            case BYTE:
                return "TINYINT";
            case SHORT:
                return "SMALLINT";
            case INTEGER:
                return "INTEGER";
            case LONG:
                return "BIGINT";
            case DOUBLE:
            case FLOAT:
                return "DOUBLE PRECISION";
            case ENUM:
                return format("%s", enumTypeName(table, field));
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
                return "TIMESTAMP";
            case DURATION:
                return "VARCHAR(32)";
            default:
                throw new OrmSqlException(format("Unkown field type '%s'. BUG!", field.getFieldType()));
        }
    }

    private String enumTypeName(Table table, Field field) {
        return format("%s_%s", table.getSqlTable(), field.getSqlName());
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
                case INSTANT:
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
        return true;
    }

    @Override
    protected boolean supportsTransactions() {
        return true;
    }
}

package com.heliorm.sql.structure;

import com.heliorm.Field;
import com.heliorm.UncaughtOrmException;
import com.heliorm.sql.Column;
import com.heliorm.sql.Table;

import java.sql.JDBCType;

import static java.lang.String.format;

public class StructureColumn implements Column {

    protected Field<?, ?, ?> field;
    private StructureTable table;

    public StructureColumn(StructureTable table, Field field) {
        this.table = table;
        this.field = field;
    }

    @Override
    public String getName() {
        return field.getSqlName();
    }

    @Override
    public JDBCType getJdbcType() {
        switch (field.getFieldType()) {
            case FLOAT:
                return JDBCType.FLOAT;
            case DOUBLE:
                return JDBCType.DOUBLE;
            case BYTE:
            case SHORT:
                return JDBCType.SMALLINT;
            case INTEGER:
                return JDBCType.INTEGER;
            case LONG:
                return JDBCType.BIGINT;
            case ENUM:
                return JDBCType.OTHER;
            case DATE:
                return JDBCType.DATE;
            case INSTANT:
                return JDBCType.TIMESTAMP;
            case BOOLEAN:
                return JDBCType.BOOLEAN;
            case STRING:
            case DURATION:
                return JDBCType.VARCHAR;
            default:
                throw new UncaughtOrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public boolean isNullable() {
        return field.isNullable();
    }

    @Override
    public boolean isKey() {
        return field.isPrimaryKey();
    }

    @Override
    public boolean isAutoIncrement() {
        return field.isAutoNumber();
    }
}

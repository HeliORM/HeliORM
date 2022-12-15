package com.heliorm.sql.postgresql;

import com.heliorm.Field;
import com.heliorm.Table;
import com.heliorm.sql.OrmSqlException;
import com.heliorm.sql.TableGenerator;

import java.util.Optional;
import java.util.StringJoiner;

import static java.lang.String.format;

public class PostgresDialectGenerator implements TableGenerator {

    @Override
    public <O> String generateSchema(Table<O> table) throws OrmSqlException {
        StringBuilder sql = new StringBuilder();
        sql.append(format("CREATE TABLE \"%s\" (\n", table.getSqlTable()));
        boolean first = true;
        StringBuilder enums = new StringBuilder();
<<<<<<< HEAD
        for (Field field : table.getFields()) {
            if (field.isCollection()) {
                continue;
            }
=======
        for (Field<O,?> field : table.getFields()) {
>>>>>>> master
            if (first) {
                first = false;
            } else {
                sql.append(",\n");
            }
            sql.append(format("\t\"%s\" ", field.getSqlName()));
            sql.append(generateFieldSql(table, field));
            if (!field.isNullable()) {
                sql.append(" NOT NULL");
            }
            if (field.getFieldType() == Field.FieldType.ENUM) {
                enums.append(format("CREATE TYPE %s as ENUM(%s);\n", enumTypeName(table, field), getEnumValues(field)));
            }
        }
        Optional<Field<O,?>> key = table.getPrimaryKey();
        if (key.isPresent()) {
            sql.append(",\n");
            sql.append(format("PRIMARY KEY (\"%s\")", key.get().getSqlName()));
        }
        sql.append(");\n");
        return enums + sql.toString();
    }

    private <O> String generateFieldSql(Table<O> table, Field<O, ?> field) throws OrmSqlException {
        switch (field.getFieldType()) {
            case BOOLEAN:
                return "BIT";
            case BYTE:
                if (field.isPrimaryKey() && field.isAutoNumber()) {
                    return "SERIAL";
                }
                return "TINYINT";
            case SHORT:
                if (field.isPrimaryKey() && field.isAutoNumber()) {
                    return "SERIAL";
                }
                return "SMALLINT";
            case INTEGER:
                if (field.isPrimaryKey() && field.isAutoNumber()) {
                    return "SERIAL";
                }
                return "INTEGER";
            case LONG:
                if (field.isPrimaryKey() && field.isAutoNumber()) {
                    return "BIGSERIAL";
                }
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
                    length = field.getLength().get();
                }
                return format("VARCHAR(%d)", length);
            }
            case DATE:
                return "DATE";
            case INSTANT:
                return "TIMESTAMP";
<<<<<<< HEAD
            case DURATION:
                return "VARCHAR(32)";
            case SET :
            case LIST:
                throw new OrmSqlException(format("Cannot generate SQL for field type '%s'. BUG!", field.getFieldType()));
=======
>>>>>>> master
            default:
                throw new OrmSqlException(format("Unkown field type '%s'. BUG!", field.getFieldType()));
        }
    }

    private <O> String enumTypeName(Table<O> table, Field<O,?> field) {
        return format("%s_%s", table.getSqlTable(), field.getSqlName());
    }

    private String getEnumValues(Field<?, ?> field) {
        StringJoiner sql = new StringJoiner(",");
        Class<?> javaType = field.getJavaType();
        for (Object v : javaType.getEnumConstants()) {
            sql.add(format("'%s'", ((Enum<?>) v).name()));
        }
        return sql.toString();
    }

}

package com.heliorm.sql.mysql;

import com.heliorm.def.Field;
import com.heliorm.sql.OrmSqlException;
import com.heliorm.Table;
import com.heliorm.sql.TableGenerator;

import java.util.Optional;
import java.util.StringJoiner;

import static java.lang.String.format;

public class MysqlDialectGenerator implements TableGenerator {
    @Override
    public String generateSchema(Table<?> table) throws OrmSqlException {
        StringBuilder sql = new StringBuilder();
        sql.append(format("CREATE TABLE %s (\n", fullTableName(table)));
        boolean first = true;
        for (Field field : table.getFields()) {
            if (first) {
                first = false;
            } else {
                sql.append(",\n");
            }
            sql.append(format("\t`%s` ", field.getSqlName()));
            sql.append(generateFieldSql(table, field));
            if (!field.isNullable()) {
                sql.append(" NOT NULL");
            }
            if (field.isPrimaryKey() && field.isAutoNumber() && field.getFieldType() != Field.FieldType.STRING) {
                sql.append(" AUTO_INCREMENT");
            }
        }
        Optional<Field> key = table.getPrimaryKey();
        if (key.isPresent()) {
            sql.append(",\n");
            sql.append(format("PRIMARY KEY (`%s`)", key.get().getSqlName()));
        }
        sql.append(");\n");
        return sql.toString();
    }

    private String generateFieldSql(Table table, Field field) throws OrmSqlException {
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
                return format("ENUM(%s)", getEnumValues(table, field));
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
            case TIMESTAMP:
                return "DATETIME";
            case DURATION:
                return "VARCHAR(32)";
            default:
                throw new OrmSqlException(format("Unkown field type '%s'. BUG!", field.getFieldType()));
        }
    }

    private String getEnumValues(Table table, Field<?, ?, ?> field) {
        StringJoiner sql = new StringJoiner(",");
        Class<?> javaType = field.getJavaType();
        for (Object v : javaType.getEnumConstants()) {
            sql.add(format("'%s'", ((Enum) v).name()));
        }
        return sql.toString();
    }

    private String fullTableName(Table table) {
        return format("`%s`.`%s`", table.getDatabase().getSqlDatabase(), table.getSqlTable());
    }
}

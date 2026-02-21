package com.heliorm.sql.mysql;

import com.heliorm.Field;
import com.heliorm.Index;
import com.heliorm.Table;
import com.heliorm.sql.TableGenerator;

import java.util.Optional;
import java.util.StringJoiner;

import static java.lang.String.format;

public class MysqlDialectGenerator implements TableGenerator {

    @Override
    public <O> String generateSchema(Table<O> table) {
        StringBuilder sql = new StringBuilder();
        sql.append(format("CREATE TABLE %s (\n", fullTableName(table)));
        boolean first = true;
        for (Field<?,?> field : table.getFields()) {
            if (first) {
                first = false;
            } else {
                sql.append(",\n");
            }
            sql.append(format("\t`%s` ", field.getSqlName()));
            sql.append(generateFieldSql(field));
            if (!field.isNullable()) {
                sql.append(" NOT NULL");
            }
            if (field.isPrimaryKey() && field.isAutoNumber() && field.getFieldType() != Field.FieldType.STRING) {
                sql.append(" AUTO_INCREMENT");
            }
        }
        Optional<Field<O,?>> key = table.getPrimaryKey();
        if (key.isPresent()) {
            sql.append(",\n");
            sql.append(format("PRIMARY KEY (`%s`)", key.get().getSqlName()));
        }

        int num = 0;
        for (Index<?> index : table.getIndexes()) {
            sql.append(",\n");
            if (index.isUnique()) {
                sql.append("UNIQUE ");
            }
            sql.append(format("INDEX %s(", format("%s_idx%d", table.getSqlTable(), num)));
            Optional<String> fields = index.getFields().stream()
                    .map(field -> "`" + field.getSqlName() + "`")
                    .reduce((s1, s2) -> s1 + "," + s2);
            fields.ifPresent(sql::append);
            sql.append(")");
            num++;
        }

        sql.append(");\n");
        return sql.toString();
    }

    private String generateFieldSql(Field<?,?> field) {
       return switch (field.getFieldType()) {
            case BOOLEAN -> "TINYINT(1)";
            case BYTE -> "TINYINT";
            case SHORT ->  "SMALLINT";
            case INTEGER ->  "INTEGER";
            case LONG ->  "BIGINT";
            case DOUBLE -> "DOUBLE";
            case FLOAT -> "REAL";
            case ENUM -> format("ENUM(%s)", getEnumValues(field));
            case STRING -> {
                int length = 255;
                if (field.isPrimaryKey()) {
                    length = 36;
                }
                if (field.getLength().isPresent()) {
                    length = field.getLength().get();
                }
                if (length >= 16777215) {
                    yield  "LONGTEXT";
                } else if (length > 65535) {
                    yield  "MEDIUMTEXT";
                } else if (length > 255) {
                    yield  "TEXT";
                } else {
                    yield  format("VARCHAR(%d)", length);
                }
            }
            case DATE -> "DATE";
            case INSTANT, LOCAL_DATE_TIME -> "DATETIME";
           case BYTE_ARRAY -> "LONGBLOB";
        };
    }

    private String getEnumValues(Field<?, ?> field) {
        var sql = new StringJoiner(",");
        Class<?> javaType = field.getJavaType();
        for (Object v : javaType.getEnumConstants()) {
            sql.add(format("'%s'", ((Enum<?>) v).name()));
        }
        return sql.toString();
    }

    private String fullTableName(Table<?> table) {
        return format("`%s`.`%s`", table.getDatabase().getSqlDatabase(), table.getSqlTable());
    }
}

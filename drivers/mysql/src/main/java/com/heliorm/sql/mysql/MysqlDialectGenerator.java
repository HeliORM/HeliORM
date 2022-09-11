package com.heliorm.sql.mysql;

import com.heliorm.Field;
import com.heliorm.Index;
import com.heliorm.Table;
import com.heliorm.sql.OrmSqlException;
import com.heliorm.sql.TableGenerator;

import java.util.Optional;
import java.util.StringJoiner;

import static java.lang.String.format;

public class MysqlDialectGenerator implements TableGenerator {

    @Override
    public <O> String generateSchema(Table<O> table) throws OrmSqlException {
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

    private String generateFieldSql(Field<?,?> field) throws OrmSqlException {
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
                    length = field.getLength().get();
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
            sql.add(format("'%s'", ((Enum<?>) v).name()));
        }
        return sql.toString();
    }

    private String fullTableName(Table<?> table) {
        return format("`%s`.`%s`", table.getDatabase().getSqlDatabase(), table.getSqlTable());
    }
}

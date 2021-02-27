package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Field;

import java.util.StringJoiner;

import static java.lang.String.format;

final class QueryHelper {

    private final SqlDriver driver;

    QueryHelper(SqlDriver driver) {
        this.driver = driver;
    }

    String buildInsertQuery(Table<?> table) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("INSERT INTO %s(", driver.fullTableName(table)));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            if (field.isPrimaryKey()) {
                if (field.isAutoNumber()) {
                    if (field.getFieldType() != Field.FieldType.STRING) {
                        continue;
                    }
                }
            }
            fields.add(format("%s", driver.fieldName(table, field)));
            values.add("?");
        }
        query.append(fields.toString());
        query.append(") VALUES(");
        query.append(values.toString());
        query.append(")");
        return query.toString();
    }

}

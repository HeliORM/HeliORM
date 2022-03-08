package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Where;
import com.heliorm.Field;
import com.heliorm.impl.ExecutablePart;
import com.heliorm.impl.ExpressionContinuationPart;
import com.heliorm.impl.ExpressionPart;
import com.heliorm.impl.IsExpressionPart;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.ListExpressionPart;
import com.heliorm.impl.OrderPart;
import com.heliorm.impl.SelectPart;
import com.heliorm.impl.ValueExpressionPart;
import com.heliorm.impl.WherePart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Helper class that builds SQL queries.
 *
 * @author gideon
 */
final class QueryHelper {

    static final String POJO_NAME_FIELD = "pojo_field_name";
    private final SqlDriver driver;
    private final Function<Field, String> getFieldId;
    private final FullTableName fullTableName;

    private static final DateFormat dateTimeFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    QueryHelper(SqlDriver driver, Function<Field, String> getFieldId, FullTableName fullTableName) {
        this.driver = driver;
        this.getFieldId = getFieldId;
        this.fullTableName = fullTableName;
    }

    String buildInsertQuery(Table<?> table) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("INSERT INTO %s(", fullTableName.apply(table)));
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
        query.append(fields);
        query.append(") VALUES(");
        query.append(values);
        query.append(")");
        return query.toString();
    }

    String buildUpdateQuery(Table<?> table) throws OrmException {
        if (!table.getPrimaryKey().isPresent()) {
            throw new OrmException("A table needs primary key for objects to be updated");
        }
        StringBuilder query = new StringBuilder();
        query.append(format("UPDATE %s SET ", fullTableName.apply(table)));
        StringJoiner fields = new StringJoiner(",");
        for (Field field : table.getFields()) {
            if (!field.isPrimaryKey()) {
                fields.add(format("%s=?", driver.fieldName(table, field)));
            }
        }
        query.append(fields);
        query.append(format(" WHERE %s=?", driver.fieldName(table, table.getPrimaryKey().get())));
        return query.toString();
    }

    String buildDeleteQuery(Table<?> table) throws OrmException {
        if (table.getPrimaryKey().isPresent()) {
            return format("DELETE FROM %s WHERE %s=?", fullTableName.apply(table), driver.fieldName(table, table.getPrimaryKey().get()));
        } else {
            throw new OrmException("A table needs primary key for objects to be deleted");
        }
    }

    String buildSelectQuery(ExecutablePart<?,?> exec) throws OrmException {
        SelectPart<?, ?> root = exec.getSelect();
        StringBuilder tablesQuery = new StringBuilder();
        tablesQuery.append("SELECT DISTINCT  ");
        StringJoiner fieldList = new StringJoiner(",");
        for (Field field : new ArrayList<Field<?,?,?>>(root.getTable().getFields())) {
            fieldList.add(format("%s AS %s", driver.fullFieldName(root.getTable(), field), driver.virtualFieldName(getFieldId(field))));
        }
        tablesQuery.append(fieldList.toString());
        tablesQuery.append(format(" FROM %s", fullTableName.apply(root.getTable()), fullTableName.apply(root.getTable())));
        StringBuilder whereQuery = new StringBuilder();
        Optional<? extends Where<?, ?>> where = root.getSelect().getWhere();
        if (where.isPresent()) {
            whereQuery.append(expandCriteria(root.getSelect().getTable(), (WherePart<?, ?>) where.get()));
        }
        for (JoinPart<?,?,?,?> join : root.getJoins()) {
            tablesQuery.append(expandLinkTables(root.getTable(), join));
            String joinWhere = expandLinkWheres(join);
            if (!joinWhere.isEmpty()) {
                if (whereQuery.length() > 0) {
                    whereQuery.append(" AND ");
                }
                whereQuery.append(expandLinkWheres(join));
            }
        }
        // finalize the query
        StringBuilder query = new StringBuilder();
        query.append(tablesQuery);
        if (whereQuery.length() > 0) {
            query.append(" WHERE ");
            query.append(whereQuery);
        }
        // do ordering
        query.append(expandOrder(root.getTable(), exec.getOrder()));
        return query.toString();
    }

    String buildSelectUnionQuery(List<ExecutablePart> queries) throws OrmException {
        Set<Field> allFields = queries.stream()
                .map(query -> query.getSelect().getTable())
                .flatMap(table -> (Stream<Field>) (table.getFields().stream()))
                .collect(Collectors.toSet());
        StringJoiner buf = new StringJoiner(" UNION ALL ");
        ExecutablePart<?,?> root = null;
        for (ExecutablePart query : queries) {
            if (root == null) {
                root = query;
            }
            buf.add(buildPartialUnionQuery(query.getSelect(), allFields));
        }
        if (root == null) {
            throw new OrmException("Could not find any parts in a union query. BUG!");
        }
        return buf.toString();
    }

    private String buildPartialUnionQuery(SelectPart<?,?> select, Set<Field> allFields) throws OrmException {
        StringBuilder tablesQuery = new StringBuilder();
        StringJoiner fieldsQuery = new StringJoiner(",");
        List<Field> tableFields = select.getTable().getFields();
        for (Field field : allFields) {
            String fieldId = getFieldId(field);
            if (tableFields.contains(field)) {
                fieldsQuery.add(format("%s AS %s", driver.fullFieldName(select.getTable(), field), driver.virtualFieldName(fieldId)));
            } else {
                String empty = driver.castNull(field);
                fieldsQuery.add(format("%s AS %s", empty, driver.virtualFieldName(fieldId)));
            }
        }
        tablesQuery.append(format("SELECT %s", fieldsQuery.toString()));
        tablesQuery.append(format(",%s AS %s", driver.virtualValue(select.getTable().getObjectClass().getName()), driver.virtualFieldName(POJO_NAME_FIELD)));
        tablesQuery.append(format(" FROM %s", fullTableName.apply(select.getTable()), fullTableName.apply(select.getTable())));
        StringBuilder whereQuery = new StringBuilder();
        Optional<? extends Where<?, ?>> where = select.getWhere();
        if (where.isPresent()) {
            whereQuery.append(expandCriteria(select.getTable(), (WherePart<?, ?>) where.get()));
        }
        for (JoinPart<?,?,?,?> join : select.getJoins()) {
            tablesQuery.append(expandLinkTables(select.getTable(), join));
            if (whereQuery.length() > 0) {
                whereQuery.append(" AND ");
            }
            whereQuery.append(expandLinkWheres(join));
        }
        if (whereQuery.length() > 0) {
            tablesQuery.append(" WHERE ");
            tablesQuery.append(whereQuery);
        }
        return tablesQuery.toString();
    }

    private String expandLinkTables(Table table, JoinPart<?,?,?,?> right) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format(" JOIN %s ON %s=%s ",
                fullTableName.apply(right.getTable()),
                driver.fullFieldName(table, right.getOn().getLeftField()),
                driver.fullFieldName(right.getTable(), right.getOn().getRightField())));
        for (JoinPart<?,?,?,?> join : right.getJoins()) {
            query.append(expandLinkTables(right.getTable(), join));
        }
        return query.toString();
    }

    private String expandLinkWheres(JoinPart<?,?,?,?> join) throws OrmException {
        String query ="";
        Optional<? extends WherePart<?, ?>> where = join.getWhere();
        if (where.isPresent()) {
            query = expandCriteria(join.getTable(), where.get());
        }
        for (JoinPart<?,?,?,?> next : join.getJoins()) {
            String linkWheres = expandLinkWheres(next);
            if (!linkWheres.isEmpty()) {
                if (!query.isEmpty()) {
                    query = query + " AND ";
                }
                query = query + linkWheres;
            }
        }
        return query;
    }

    private String expandCriteria(Table<?> table, WherePart<?,?> where) throws OrmException {
        ExpressionPart<?, ?, ?> expr = where.getExpression();
        StringBuilder query = new StringBuilder();
        query.append(expandExpression(table, expr));
        for (ExpressionContinuationPart<?,?> ec : where.getContinuations()) {
            switch (ec.getType()) {
                case AND:
                    query.append(" AND ");
                    break;
                case OR:
                    query.append(" OR ");
                    break;
                default:
                    throw new OrmException(format("Unknown continuation type '%s'. BUG!", expr.getType()));
            }
            query.append(expandExpression(table, ec.getExpression()));
        }
        return query.toString();
    }

    private String expandExpression(Table table, ExpressionPart expr) throws OrmException {
        switch (expr.getType() ) {
            case LIST_EXPRESSION:
              return  expandListFieldCriteria(table, ((ListExpressionPart) expr));
            case VALUE_EXPRESSION:
                return expandValueFieldCriteria(table, (ValueExpressionPart) expr);
            case IS_EXPRESSION:
                return expandIsFieldCriteria(table, (IsExpressionPart) expr);
            default:
                throw new OrmException(format("Unknown expression type '%s'. BUG!", expr.getType()));
        }
    }

    private String expandListFieldCriteria(Table table,  ListExpressionPart crit) throws OrmException {
        StringJoiner list = new StringJoiner(",");
        if (crit.getValues().isEmpty()) {
            throw new OrmException(format("Empty %s list for field %s in table %s", crit.getOperator(),
                    crit.getField().getJavaName(), table.getObjectClass().getSimpleName()));
        }
        for (Object val : crit.getValues()) {
            list.add(format("'%s'", sqlValue(crit.getField(), val)));
        }
        return format("%s %s (%s)", driver.fullFieldName(table, crit.getField()), listOperator(crit), list);
    }

    private String expandValueFieldCriteria(Table table, ValueExpressionPart crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        if (crit.getValue() == null) {
            throw new OrmException(format("Null %s value for field %s in table %s", crit.getOperator(),
                    crit.getField().getJavaName(), table.getObjectClass().getSimpleName()));
        }
        query.append(format("%s%s'%s'", driver.fullFieldName(table, crit.getField()), valueOperator(crit), sqlValue(crit.getField(), crit.getValue())));
        return query.toString();
    }

    private String expandIsFieldCriteria(Table table, IsExpressionPart crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s%s", driver.fullFieldName(table, crit.getField()), isOperator(crit)));
        return query.toString();
    }

    /**
     * Expand the given order part into the fields of a SQL order clause.
     *
     * @param table The table spec to which the ordering applies
     * @return The partial SQL query string
     */
    private String expandOrder(Table table,  List<? extends OrderPart<?,?>> orders) throws OrmException {
        StringBuilder query = new StringBuilder();
        if (!orders.isEmpty()) {
            query.append(" ORDER BY ");
        }
        StringBuilder body = new StringBuilder();
        for (OrderPart<?, ?> order : orders) {
            if (body.length() > 0) {
                body.append(", ");
            }
            body.append(format("%s", driver.fieldName(table, order.getField())));
            if (order.getDirection() == OrderPart.Direction.DESCENDING) {
                body.append(" DESC");
            }
        }
        query.append(body);
        return query.toString();
    }

    private String sqlValue(Field field, Object object) {
        switch (field.getFieldType()) {
            case DATE:
                return dateTimeFormat.format(object);
            default:
                return object.toString();
        }
    }

    /**
     * Return the SQL operator for the given list criteria part's operator.
     *
     * @param part The part
     * @return The operator
     * @throws OrmException
     */
    private String listOperator(ListExpressionPart part) throws OrmException {
        switch (part.getOperator()) {
            case IN:
                return " IN";
            case NOT_IN:
                return " NOT IN";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", part.getOperator()));
        }
    }

    /**
     * Return the SQL operator for the given value criteria part's operator.
     *
     * @param part The part
     * @return The operator
     * @throws OrmException
     */
    private String valueOperator(ValueExpressionPart part) throws OrmException {
        switch (part.getOperator()) {
            case EQ:
                return "=";
            case NOT_EQ:
                return "<>";
            case GE:
                return ">=";
            case LE:
                return "<=";
            case GT:
                return ">";
            case LT:
                return "<";
            case LIKE:
                return " LIKE ";
            case NOT_LIKE:
                return " NOT LIKE ";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", part.getOperator()));
        }
    }

    private String isOperator(IsExpressionPart part) throws OrmException {
        switch (part.getOperator()) {
            case IS_NULL:
                return " IS NULL";
            case IS_NOT_NULL:
                return " IS NOT NULL";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", part.getOperator()));
        }
    }

    private String getFieldId(Field field) {
        return getFieldId.apply(field);
    }

}

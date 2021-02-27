package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.impl.Part;
import com.heliorm.query.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

final class QueryHelper {

    static final String POJO_NAME_FIELD = "pojo_field_name";
    private final SqlDriver driver;
    private final Function<Field, String> getFieldId;

    QueryHelper(SqlDriver driver, Function<Field, String> getFieldId) {
        this.driver = driver;
        this.getFieldId = getFieldId;
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

    String buildUpdateQuery(Table<?> table) throws OrmException {
        if (!table.getPrimaryKey().isPresent()) {
            throw new OrmException("A table needs primary key for objects to be updated");
        }
        StringBuilder query = new StringBuilder();
        query.append(format("UPDATE %s SET ", driver.fullTableName(table)));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            if (!field.isPrimaryKey()) {
                fields.add(format("%s=?", driver.fieldName(table, field)));
            }
        }
        query.append(fields.toString());
        query.append(format(" WHERE %s=?", driver.fieldName(table, table.getPrimaryKey().get())));
        return query.toString();
    }

    String buildDeleteQuery(Table<?> table) throws OrmException {
        if (table.getPrimaryKey().isPresent()) {
            return format("DELETE FROM %s WHERE %s=?", driver.fullTableName(table), driver.fieldName(table, table.getPrimaryKey().get()));
        } else {
            throw new OrmException("A table needs primary key for objects to be deleted");
        }
    }

    String buildSelectQuery(Query root) throws OrmException {
        StringBuilder tablesQuery = new StringBuilder();
        tablesQuery.append("SELECT DISTINCT  ");
        StringJoiner fieldList = new StringJoiner(",");
        for (Field field : root.getTable().getFields()) {
            fieldList.add(format("%s AS %s", driver.fullFieldName(root.getTable(), field), driver.virtualFieldName(getFieldId(field))));
        }
        tablesQuery.append(fieldList.toString());
        tablesQuery.append(format(" FROM %s", driver.fullTableName(root.getTable()), driver.fullTableName(root.getTable())));
        StringBuilder whereQuery = new StringBuilder();
        Optional<Criteria> optCrit = root.getCriteria();
        if (optCrit.isPresent()) {
            whereQuery.append(expandCriteria(root, optCrit.get()));
        }
        Optional<Link> optLink = root.getLink();
        if (optLink.isPresent()) {
            tablesQuery.append(expandLinkTables(root, optLink.get()));
            if (whereQuery.length() > 0) {
                whereQuery.append(" AND ");
            }
            whereQuery.append(expandLinkWheres(optLink.get()));
        }
        // finalize the query
        StringBuilder query = new StringBuilder();
        query.append(tablesQuery);
        if (whereQuery.length() > 0) {
            query.append(" WHERE ");
            query.append(whereQuery);
        }
        // do ordering
        Optional<Order> optOrder = root.getOrder();
        if (optOrder.isPresent()) {
            query.append(" ORDER BY ");
            query.append(expandOrder(root, optOrder.get()));
        }
        return query.toString();
    }

     String buildSelectUnionQuery(List<List<Part>> queries) throws OrmException {
        Set<Field> allFields = queries.stream()
                .map(parts -> parts.get(0).getReturnTable())
                .flatMap(table -> (Stream<Field>) (table.getFields().stream()))
                .collect(Collectors.toSet());
        StringJoiner buf = new StringJoiner(" UNION ALL ");
        Query root = null;
        for (List<Part> parts : queries) {
            root = Parser.parse(parts);
            StringBuilder tablesQuery = new StringBuilder();
            StringJoiner fieldsQuery = new StringJoiner(",");
            List<Field> tableFields = root.getTable().getFields();
            for (Field field : allFields) {
                String fieldId = getFieldId(field);
                if (tableFields.contains(field)) {
                    fieldsQuery.add(format("%s AS %s", driver.fullFieldName(root.getTable(), field), driver.virtualFieldName(fieldId)));
                } else {
                    String empty = "NULL";
                    fieldsQuery.add(format("%s AS %s", empty, driver.virtualFieldName(fieldId)));
                }
            }
            tablesQuery.append(format("SELECT %s", fieldsQuery.toString()));
            tablesQuery.append(format(",%s AS %s", driver.virtualValue(root.getTable().getObjectClass().getName()), driver.virtualFieldName(POJO_NAME_FIELD)));
            tablesQuery.append(format(" FROM %s", driver.fullTableName(root.getTable()), driver.fullTableName(root.getTable())));
            StringBuilder whereQuery = new StringBuilder();
            Optional<Criteria> optCrit = root.getCriteria();
            if (optCrit.isPresent()) {
                whereQuery.append(expandCriteria(root, optCrit.get()));

            }
            Optional<Link> optLink = root.getLink();
            if (optLink.isPresent()) {
                tablesQuery.append(expandLinkTables(root, optLink.get()));
                if (whereQuery.length() > 0) {
                    whereQuery.append(" AND ");
                }
                whereQuery.append(expandLinkWheres(optLink.get()));
            }
            if (whereQuery.length() > 0) {
                tablesQuery.append(" WHERE ");
                tablesQuery.append(whereQuery);
            }
            buf.add(tablesQuery.toString());
        }
        StringBuilder query = new StringBuilder(buf.toString());
        // do ordering
        Optional<Order> optOrder = root.getOrder();
        if (optOrder.isPresent()) {
            query.append(" ORDER BY ");
            query.append(expandOrder(root, optOrder.get()));
        }
        return query.toString();
    }


    private String expandLinkTables(TableSpec left, Link right) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format(" JOIN %s ON %s=%s ",
                driver.fullTableName(right.getTable()),
                driver.fullFieldName(left.getTable(), right.getLeftField()),
                driver.fullFieldName(right.getTable(), right.getField())));
        if (right.getLink().isPresent()) {
            query.append(expandLinkTables(right, right.getLink().get()));
        }
        return query.toString();
    }

    private String expandLinkWheres(Link link) throws OrmException {
        StringBuilder query = new StringBuilder();
        Optional<Criteria> optCrit = link.getCriteria();
        if (optCrit.isPresent()) {
            query.append(expandCriteria(link, optCrit.get()));
        }
        if (link.getLink().isPresent()) {
            query.append(expandLinkWheres(link.getLink().get()));
        }
        return query.toString();

    }

    private String expandCriteria(TableSpec table, Criteria crit) throws OrmException {
        switch (crit.getType()) {
            case LIST_FIELD:
                return expandListFieldCriteria(table, (ListCriteria) crit);
            case VALUE_FIELD:
                return expandValueFieldCriteria(table, (ValueCriteria) crit);
            case IS_FIELD:
                return expandIsFieldCriteria(table, (IsCriteria) crit);
            case AND:
                AndCriteria and = (AndCriteria) crit;
                return format("(%s AND %s)", expandCriteria(table, and.getLeft()), expandCriteria(table, and.getRight()));
            case OR:
                OrCriteria or = (OrCriteria) crit;
                return format("(%s OR %s)", expandCriteria(table, or.getLeft()), expandCriteria(table, or.getRight()));
            default:
                throw new OrmException(format("Unexpected criteria type '%s' in switch. BUG!", crit.getType()));
        }
    }

    private String expandListFieldCriteria(TableSpec table, ListCriteria crit) throws OrmException {
        StringJoiner list = new StringJoiner(",");
        for (Object val : crit.getValues()) {
            list.add(format("'%s'", sqlValue(val)));
        }
        return format("%s %s (%s)", driver.fullFieldName(table.getTable(), crit.getField()), listOperator(crit), list.toString());
    }

    private String expandValueFieldCriteria(TableSpec table, ValueCriteria crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s%s'%s'", driver.fullFieldName(table.getTable(), crit.getField()), valueOperator(crit), sqlValue(crit.getValue())
        ));
        return query.toString();
    }

    private String expandIsFieldCriteria(TableSpec table, IsCriteria crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s%s", driver.fullFieldName(table.getTable(), crit.getField()), isOperator(crit)));
        return query.toString();
    }

    /**
     * Expand the given order part into the fields of a SQL order clause.
     *
     * @param table The table spec to which the ordering applies
     * @param order The order part
     * @return The partial SQL query string
     */
    private String expandOrder(TableSpec table, Order order) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s", driver.fieldName(table.getTable(), order.getField())));
        if (order.getDirection() == Order.Direction.DESCENDING) {
            query.append(" DESC");
        }
        if (order.getThenBy().isPresent()) {
            query.append(", ");
            query.append(expandOrder(table, order.getThenBy().get()));
        }
        return query.toString();
    }

    private String sqlValue(Object object) {
        return object.toString();
    }

    /**
     * Return the SQL operator for the given list criteria part's operator.
     *
     * @param part The part
     * @return The operator
     * @throws OrmException
     */
    private String listOperator(ListCriteria part) throws OrmException {
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
    private String valueOperator(ValueCriteria part) throws OrmException {
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

    private String isOperator(IsCriteria part) throws OrmException {
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

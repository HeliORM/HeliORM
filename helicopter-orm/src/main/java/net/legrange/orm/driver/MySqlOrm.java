package net.legrange.orm.driver;

import static java.lang.String.format;
import java.sql.Connection;
import java.util.Optional;
import java.util.StringJoiner;
import net.legrange.orm.Field;
import net.legrange.orm.Orm;
import net.legrange.orm.OrmException;
import net.legrange.orm.Table;
import net.legrange.orm.rep.AndCriteria;
import net.legrange.orm.rep.Criteria;
import net.legrange.orm.rep.Link;
import net.legrange.orm.rep.ListCriteria;
import net.legrange.orm.rep.OrCriteria;
import net.legrange.orm.rep.Order;
import net.legrange.orm.rep.Query;
import net.legrange.orm.rep.TableSpec;
import net.legrange.orm.rep.ValueCriteria;

/**
 *
 * @author gideon
 */
public class MySqlOrm extends Orm {

    public MySqlOrm(Connection con) throws OrmException {
        super(con);
    }

    @Override
    protected String buildInsertQuery(Table<?> table) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("INSERT INTO %s(", table.getSqlTable()));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            fields.add(field.getSqlName());
            values.add("?");
        }
        query.append(fields.toString());
        query.append(") VALUES(");
        query.append(values.toString());
        query.append(")");
        return query.toString();
    }

    @Override
    protected String buildUpdateQuery(Table<?> table) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("UPDATE %s SET ", table.getSqlTable()));
        StringJoiner fields = new StringJoiner(",");
        StringJoiner values = new StringJoiner(",");
        for (Field field : table.getFields()) {
            if (!field.isPrimaryKey()) {
                fields.add(format("%s=?", field.getSqlName()));
            }
        }
        query.append(fields.toString());
        query.append(format(" WHERE %s=?", table.getPrimaryKey().get().getSqlName()));
        return query.toString();
    }

    @Override
    protected String buildDeleteQuery(Table<?> table) throws OrmException {
        return format("DELETE FROM %s WHERE %s=?", table.getSqlTable(), table.getPrimaryKey().get().getSqlName());
    }

    @Override
    protected String buildSelectQuery(Query root) throws OrmException {
        StringBuilder tablesQuery = new StringBuilder();
        tablesQuery.append(format("SELECT %s.* FROM %s", root.getTable().getSqlTable(), root.getTable().getSqlTable()));
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

    private String expandLinkTables(TableSpec left, Link right) {
        StringBuilder query = new StringBuilder();
        query.append(format(" JOIN %s ON %s.%s=%s.%s ",
                right.getTable().getSqlTable(),
                left.getTable().getSqlTable(), right.getLeftField().getSqlName(),
                right.getTable().getSqlTable(), right.getField().getSqlName()));
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
        StringBuilder query = new StringBuilder();
        query.append(format("%s.%s %s (", table.getTable().getSqlTable(), crit.getField().getSqlName(), listOperator(crit)));
        for (Object val : crit.getValues()) {
            query.append(format("'%s'", sqlValue(val)));
        }
        query.append("'");
        return query.toString();
    }

    private String expandValueFieldCriteria(TableSpec table, ValueCriteria crit) throws OrmException {
        StringBuilder query = new StringBuilder();
        query.append(format("%s.%s%s'%s'", table.getTable().getSqlTable(), crit.getField().getSqlName(), valueOperator(crit), sqlValue(crit.getValue())
        ));
        return query.toString();
    }

    private String expandOrder(TableSpec table, Order order) {
        StringBuilder query = new StringBuilder();
        query.append(format("%s.%s", table.getTable().getSqlTable(), order.getField().getSqlName()));
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

}
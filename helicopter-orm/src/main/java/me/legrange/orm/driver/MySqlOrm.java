package me.legrange.orm.driver;

import static java.lang.String.format;
import java.sql.Connection;
import java.util.StringJoiner;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import me.legrange.orm.impl.FieldPart;
import me.legrange.orm.impl.JoinPart;
import me.legrange.orm.impl.ListExpressionPart;
import me.legrange.orm.impl.Node;
import me.legrange.orm.impl.OnClausePart;
import me.legrange.orm.impl.OrderedPart;
import me.legrange.orm.impl.Part;
import me.legrange.orm.impl.ValueExpressionPart;

/**
 *
 * @author gideon
 */
public class MySqlOrm extends Orm {

    public MySqlOrm(Connection con) {
        super(con);
    }

    @Override
    protected String buildQuery(Node root) throws OrmException {
        StringBuilder query = new StringBuilder();
        Node node = root;
        while (node != null) {
            query.append(buildPartQuery(node));
            node = node.getNext();
        }
        return query.toString();

    }

    private String buildPartQuery(Node node) throws OrmException {
        Part part = node.getPart();
        switch (part.getType()) {
            case SELECT: {
                if (part.left() == null) {
                    return format("SELECT %s.* FROM %s",
                            part.getReturnTable().getSqlTable(),
                            part.getReturnTable().getSqlTable());
                }
            }
            break;
            case WHERE:
                return format(" WHERE (%s)", buildExpression(node));
            case JOIN:
                return format(" JOIN %s ",
                        ((JoinPart) part).getTable().getSqlTable());
            case ON_CLAUSE: {
                OnClausePart on = (OnClausePart) part;
                return format(" ON %s.%s=%s.%s",
                        part.left().getSelectTable().getSqlTable(),
                        on.getLeftField().getSqlName(),
                        part.getSelectTable().getSqlTable(),
                        on.getRightField().getSqlName());
            }
            case ORDER: {
                OrderedPart order = (OrderedPart) part;
                StringBuilder buf = new StringBuilder();

                if (part.left().getType() != Part.Type.ORDER) {
                    buf.append(" ORDER BY ");
                } else {
                    buf.append(",");
                }
                buf.append(order.getField().getSqlName());
                if (order.getDirection() == OrderedPart.Direction.DESCENDING) {
                    buf.append(" DESCENDING");
                }
                return buf.toString();
            }
            case AND:
                return format(" AND (%s)",
                        buildExpression(node));
            case OR:
                return format(" OR (%s)",
                        buildExpression(node));
            case NESTED_AND:
                return format(" AND %s",
                        buildExpression(node));
            case NESTED_OR:
                return format(" OR %s",
                        buildExpression(node));
            case VALUE_EXPRESSION: {
                ValueExpressionPart vop = (ValueExpressionPart) part;
                return format("%s '%s'", valueOperator(vop),
                        sqlValue(vop.getValue()));
            }
            case LIST_EXPRESSION: {
                ListExpressionPart lop = (ListExpressionPart) part;
                StringJoiner sj = new StringJoiner(",");
                for (Object value : lop.getValues()) {
                    sj.add(format("'%s'", sqlValue(value)));
                }
                return format("%s(%s)", listOperator(lop), sj.toString());
            }
            case FIELD: {
                FieldPart field = (FieldPart) part;
                return format("%s", field.getSqlName());
            }
            default:
                throw new OrmException(format("Unsupported part type '%s'. BUG!", part.getType()));
        }
        return "";
    }

    private String buildExpression(Node node) throws OrmException {
        return format("%s", buildQuery(node.getTangent()));
    }

    private String sqlValue(Object object) {
        return object.toString();
    }

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
                return " LIKE";
            case NOT_LIKE:
                return " NOT LIKE";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", part.getOperator()));
        }

    }

}

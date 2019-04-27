package me.legrange.orm.driver;

import static java.lang.String.format;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import me.legrange.orm.impl.ContinuationPart;
import me.legrange.orm.impl.ExpressionContinuationPart;
import me.legrange.orm.impl.ExpressionPart;
import me.legrange.orm.impl.FieldPart;
import me.legrange.orm.impl.JoinPart;
import me.legrange.orm.impl.ListExpressionPart;
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
    protected String buildQuery(List<Part> parts) throws OrmException {
        StringBuilder query = new StringBuilder();
        for (Part part : parts) {
            query.append(buildPartQuery(part));

        }
        return query.toString();

    }

    private String buildPartQuery(Part part) throws OrmException {
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
                return format(" WHERE %s", buildExpression(part));
//                        buildExpression(((ContinuationPart) part).getExpression()));
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
                return format(" AND %s",
                        buildExpression(part));
            case OR:
                return format(" OR %s",
                        buildExpression(part));
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

    private String buildExpression(Part part) throws OrmException {
        ExpressionPart expr;
        if (part instanceof ContinuationPart) {
            expr = ((ContinuationPart) part).getExpression();
        } else if (part instanceof ExpressionContinuationPart) {
            expr = ((ExpressionContinuationPart) part).getExpression();
        } else {
            throw new OrmException(format("Cannot extract expression from %s. BUG!", part.getClass().getSimpleName()));
        }
        return buildQuery(unrollTo(expr, part));
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

    private List<Part> unrollTo(Part part, Part head) {
        List<Part> parts = new ArrayList();
        while ((part != null) && (part != head)) {
            parts.add(0, part);
            part = part.left();
        }
        return parts;
    }
}

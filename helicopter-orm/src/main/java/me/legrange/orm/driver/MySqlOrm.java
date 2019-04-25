package me.legrange.orm.driver;

import static java.lang.String.format;
import java.sql.Connection;
import java.util.List;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import me.legrange.orm.impl.ContinuationPart;
import me.legrange.orm.impl.JoinPart;
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

    private String buildPartQuery(Part part) {
        switch (part.getType()) {
            case SELECT: {
                if (part.getLeft() == null) {
                    return format("SELECT %s.* FROM ",
                            part.getReturnTable().getSqlTable(),
                            part.getReturnTable().getSqlTable());
                }
            }
            break;
            case WHERE:
                return format(" WHERE %s",
                        buildExpression((ContinuationPart) part));
            case JOIN:
                return format(" JOIN %s ",
                        ((JoinPart) part).getTable().getSqlTable());
            case ON_CLAUSE: {
                OnClausePart on = (OnClausePart) part;
                return format(" ON %s ")
                query.append(" ON ");
                query.append(part.getLeft().getSelectTable().getSqlTable());
                query.append(".");
                query.append(on.getLeftField().getSqlName());
                query.append("=");
                query.append(part.getSelectTable().getSqlTable());
                query.append(".");
                query.append(on.getRightField().getSqlName());
            }
            break;
            case ORDER: {
                OrderedPart order = (OrderedPart) part;
                if (part.getLeft().getType() != Part.Type.ORDER) {
                    query.append(" ORDER BY ");
                } else {
                    query.append(",");
                }
                query.append(order.getField().getSqlName());
                if (order.getDirection() == OrderedPart.Direction.DESCENDING) {
                    query.append(" DESCENDING");
                }
            }
            break;
            case AND:
                query.append(" AND ");
                buildExpression((ContinuationPart) part);
                break;
            case OR:
                query.append(" OR ");
                buildExpression((ContinuationPart) part);
                break;
            default:
                throw new OrmException(format("Unsupported part type '%s'. BUG!", part.getType()));
        }

    }

    private String buildExpression(ContinuationPart part) {
        switch (part.getType()) {
            case VALUE_EXPRESSION:
                ValueExpressionPart vop = (ValueExpressionPart) part;
                query.append(valueOperator(vop.getOp()));
                query.append("'");
                query.append(sqlValue(vop.getValue()));
                query.append("'");
        }
        break;
//                case LIST_OPERATION: {
//                    ListOperatorPart lop = (ListOperatorPart) part;
//                    query.append(listOperator(lop.getOp()));
//                    query.append("(");
//                    StringJoiner sj = new StringJoiner(",");
//                    for (Object value : lop.getValues()) {
//                        sj.add(format("'%s'", sqlValue(value)));
//                    }
//                    query.append(sj.toString());
//                    query.append(")");
//
//                }
    }

    private String sqlValue(Object object) {
        return object.toString();
    }
//
//    private String listOperator(ListOperatorPart.Operator op) throws OrmException {
//        switch (op) {
//            case IN:
//                return " IN";
//            case NOT_IN:
//                return " NOT IN";
//            default:
//                throw new OrmException(format("Unsupported operator '%s'. BUG!", op));
//
//        }
//
//    }
//
//    private String valueOperator(ValueOperatorPart.Operator op) throws OrmException {
//        switch (op) {
//            case EQ:
//                return "=";
//            case NOT_EQ:
//                return "<>";
//            case GE:
//                return ">=";
//            case LE:
//                return "<=";
//            case GT:
//                return ">";
//            case LT:
//                return "<";
//            case LIKE:
//                return " LIKE";
//            case NOT_LIKE:
//                return " NOT LIKE";
//            default:
//                throw new OrmException(format("Unsupported operator '%s'. BUG!", op));
//        }
//
//    }
//
//    private String clauseOperator(ClausePart.Operator op) throws OrmException {
//        switch (op) {
//            case WHERE:
//                return "WHERE";
//            case AND:
//                return "AND";
//            case OR:
//                return "AND";
//            default:
//                throw new OrmException(format("Unsupported operator '%s'. BUG!", op));
//        }
//    }
}

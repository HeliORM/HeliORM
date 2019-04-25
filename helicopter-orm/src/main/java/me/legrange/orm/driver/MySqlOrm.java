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
                return format(" ON %s.%s=%s.%s",
                        part.getLeft().getSelectTable().getSqlTable(),
                        on.getLeftField().getSqlName(),
                        part.getSelectTable().getSqlTable(),
                        on.getRightField().getSqlName());
            }
            case ORDER: {
                OrderedPart order = (OrderedPart) part;
                StringBuilder buf = new StringBuilder();

                if (part.getLeft().getType() != Part.Type.ORDER) {
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
                        buildExpression((ContinuationPart) part));
                break;
            case OR:
                return format(" OR %s",
                        buildExpression((ContinuationPart) part));
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

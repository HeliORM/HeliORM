package me.legrange.orm.driver;

import static java.lang.String.format;
import java.sql.Connection;
import java.util.List;
import java.util.StringJoiner;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import me.legrange.orm.impl.ClausePart;
import me.legrange.orm.impl.JoinPart;
import me.legrange.orm.impl.ListOperatorPart;
import me.legrange.orm.impl.OnClausePart;
import me.legrange.orm.impl.OrderedPart;
import me.legrange.orm.impl.Part;
import me.legrange.orm.impl.ValueOperatorPart;

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
            switch (part.getType()) {
                case SELECT: {
                    if (part.getLeft() == null) {
                        query.append("SELECT  ");
                        query.append(part.getReturnTable().getSqTable());
                        query.append(".* FROM ");
                        query.append(part.getReturnTable().getSqTable());
                    }
                }
                break;
                case CLAUSE: {
                    ClausePart cp = (ClausePart) part;
                    query.append(" ");
                    query.append(clauseOperator(cp.getOperator()));
                    query.append(" ");
                    query.append(cp.getField().getSqlField());
                }
                break;
                case JOIN:
                    query.append(" JOIN ");
                    query.append(((JoinPart) part).getTable().getSqTable());
                    break;
                case ON_CLAUSE: {
                    OnClausePart on = (OnClausePart) part;
                    query.append(" ON ");
                    query.append(part.getLeft().getSelectTable().getSqTable());
                    query.append(".");
                    query.append(on.getLeftField().getSqlField());
                    query.append("=");
                    query.append(part.getSelectTable().getSqTable());
                    query.append(".");
                    query.append(on.getRightField().getSqlField());
                }
                break;
                case VALUE_OPERATION: {
                    ValueOperatorPart vop = (ValueOperatorPart) part;
                    query.append(valueOperator(vop.getOp()));
                    query.append("'");
                    query.append(sqlValue(vop.getValue()));
                    query.append("'");
                }
                break;
                case LIST_OPERATION: {
                    ListOperatorPart lop = (ListOperatorPart) part;
                    query.append(listOperator(lop.getOp()));
                    query.append("(");
                    StringJoiner sj = new StringJoiner(",");
                    for (Object value : lop.getValues()) {
                        sj.add(format("'%s'", sqlValue(value)));
                    }
                    query.append(sj.toString());
                    query.append(")");

                }
                break;
                case ORDER: {
                    OrderedPart order = (OrderedPart) part;
                    if (part.getLeft().getType() != Part.Type.ORDER) {
                        query.append(" ORDER BY ");
                    } else {
                        query.append(",");
                    }
                    query.append(order.getField().getSqlField());
                    if (order.getDirection() == OrderedPart.Direction.DESCENDING) {
                        query.append(" DESCENDING");
                    }
                }
                break;
                case CONTINUATION:
                    break;
                default:
                    throw new OrmException(format("Unsupported part type '%s'. BUG!", part.getType()));
            }

        }
        return query.toString();

    }

    private String sqlValue(Object object) {
        return object.toString();
    }

    private String listOperator(ListOperatorPart.Operator op) throws OrmException {
        switch (op) {
            case IN:
                return " IN";
            case NOT_IN:
                return " NOT IN";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", op));

        }

    }

    private String valueOperator(ValueOperatorPart.Operator op) throws OrmException {
        switch (op) {
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
                throw new OrmException(format("Unsupported operator '%s'. BUG!", op));
        }

    }

    private String clauseOperator(ClausePart.Operator op) throws OrmException {
        switch (op) {
            case WHERE:
                return "WHERE";
            case AND:
                return "AND";
            case OR:
                return "AND";
            default:
                throw new OrmException(format("Unsupported operator '%s'. BUG!", op));
        }
    }
}

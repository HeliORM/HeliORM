package com.heliorm.query;

import com.heliorm.impl.ExpressionContinuationPart;
import com.heliorm.impl.FieldPart;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.ListExpressionPart;
import com.heliorm.impl.OrderedPart;
import com.heliorm.impl.SelectPart;
import com.heliorm.impl.ValueExpressionPart;
import com.heliorm.OrmException;
import com.heliorm.impl.ContinuationPart;
import com.heliorm.impl.IsExpressionPart;
import com.heliorm.impl.OnClausePart;
import com.heliorm.impl.Part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static java.lang.String.format;

/**
 * A "parser" that takes a list of parts and turns it into a simpler query structure.
 *
 * @author gideon
 */
public class Parser {

    private List<Part> parts;
    private Part part;
    private int idx = -1;
    private final Stack<List<Part>> partsStack = new Stack();
    private final Stack<Integer> idxStack = new Stack();

    public static Query parse(List<Part> parts) throws OrmException {
        return new Parser(parts).parse();
    }

    /** Private constructor, only used in here. */
    private Parser(List<Part> parts) {
        this.parts = new ArrayList(parts);
    }

    private Query parse() throws OrmException {
        next();
        expect(Part.Type.SELECT);
        Query query = new Query(((SelectPart) part).getReturnTable());
        while (hasNext()) {
            next();
            switch (part.getType()) {
                case WHERE:
                    query.setCriteria(where());
                    break;
                case AND:
                    query.setCriteria(new AndCriteria(query.getCriteria().get(), and()));
                    break;
                case OR:
                    query.setCriteria(new OrCriteria(query.getCriteria().get(), or()));
                    break;
                case JOIN:
                    query.setLink(join(query));
                    break;
                case ORDER:
                    query.setOrder(order());
                    break;
                default:
                    throw new OrmException(format("Unexpected part of type '%s'. BUG!", part.getType()));
            }
        }
        return query;
    }

    private Criteria where() throws OrmException {
        expect(Part.Type.WHERE);
        return continuation();
    }

    private Criteria and() throws OrmException {
        expect(Part.Type.AND);
        return continuation();
    }

    private Criteria or() throws OrmException {
        expect(Part.Type.OR);
        return continuation();
    }

    private Criteria continuation() throws OrmException {
        Part expr = ((ContinuationPart) part).getExpression();
        push(unroll(expr));
        Criteria crit = expression();
        pop();
        return crit;
    }

    private Link join(Query query) throws ParseException, OrmException {
        expect(Part.Type.JOIN);
        Part join = ((JoinPart) part);
        next();
        expect(Part.Type.ON_CLAUSE);
        OnClausePart on = (OnClausePart) part;
        Link link = new Link(join.getSelectTable(), on.getLeftField(), on.getRightField());
        while (hasNext()) {
            next();
            switch (part.getType()) {
                case WHERE:
                    link.setCriteria(where());
                    break;
                case AND:
                    link.setCriteria(new AndCriteria(link.getCriteria().get(), and()));
                    break;
                case OR:
                    link.setCriteria(new OrCriteria(link.getCriteria().get(), or()));
                    break;
                case JOIN:
                    link.setLink(join(query));
                    break;
                case ORDER:
                    query.setOrder(order());
                    break;
                default:
                    throw new OrmException(format("Unexpected part of type '%s'. BUG!", part.getType()));
            }
        }
        return link;
    }

    private Order order() throws ParseException {
        expect(Part.Type.ORDER);
        OrderedPart op = (OrderedPart) part;
        Order order = new Order(op.getField(), mapDirection(op.getDirection()));
        if (hasNext()) {
            next();
            order.setThenBy(order());
        }
        return order;
    }

    private Criteria expression() throws ParseException, OrmException {
        expect(Part.Type.FIELD, Part.Type.NESTED_AND, Part.Type.NESTED_OR);
        FieldPart fieldPart = ((FieldPart) part);
        next();
        expect(Part.Type.LIST_EXPRESSION, Part.Type.VALUE_EXPRESSION, Part.Type.IS_EXPRESSION);
        Criteria crit;

        switch (part.getType()) {
            case VALUE_EXPRESSION:
                expect(Part.Type.VALUE_EXPRESSION);
                ValueExpressionPart vep = (ValueExpressionPart) part;
                crit = new ValueCriteria(fieldPart.getThis(), mapOperator(vep.getOperator()), vep.getValue());
                break;
            case LIST_EXPRESSION:
                expect(Part.Type.LIST_EXPRESSION);
                ListExpressionPart lep = (ListExpressionPart) part;
                crit = new ListCriteria(fieldPart.getThis(), mapOperator(lep.getOperator()), lep.getValues());
                break;
            case IS_EXPRESSION:
                expect(Part.Type.IS_EXPRESSION);
                IsExpressionPart iep = (IsExpressionPart)part;
                crit = new IsCriteria(fieldPart.getThis(), mapOperator(iep.getOperator()));
                break;
            default:
                throw new ParseException(format("Don't know how to parse expression of type %s. BUG!", part.getType()));
        }
        if (hasNext()) {
            next();
            switch (part.getType()) {
                case NESTED_AND:
                    return and(crit);
                case NESTED_OR:
                    return or(crit);
                default:
                    throw new ParseException("");
            }
        }
        return crit;
    }

    private Criteria and(Criteria left) throws OrmException {
        expect(Part.Type.NESTED_AND);
        return new AndCriteria(left, expressionContinuation());
    }

    private Criteria or(Criteria left) throws OrmException {
        expect(Part.Type.NESTED_OR);
        return new OrCriteria(left, expressionContinuation());

    }

    private Criteria expressionContinuation() throws OrmException {
        ExpressionContinuationPart ecp = (ExpressionContinuationPart) part;
        push(unroll(ecp.getExpression()));
        Criteria crit = expression();
        pop();
        return crit;
    }

    private ListCriteria.Operator mapOperator(ListExpressionPart.Operator op) {
        return ListCriteria.Operator.valueOf(op.name());
    }

    private ValueCriteria.Operator mapOperator(ValueExpressionPart.Operator op) {
        return ValueCriteria.Operator.valueOf(op.name());
    }

    private IsCriteria.Operator mapOperator(IsExpressionPart.Operator op) {
        return IsCriteria.Operator.valueOf(op.name());
    }


    private Order.Direction mapDirection(OrderedPart.Direction dir) {
        return Order.Direction.valueOf(dir.name());
    }

    private void push(List<Part> more) {
        partsStack.push(parts);
        idxStack.push(idx);
        parts = more;
        idx = 0;
        part = parts.get(0);
    }

    private void pop() {
        parts = partsStack.pop();
        idx = idxStack.pop();
        part = parts.get(idx);
    }

    private void expect(Part.Type... types) throws ParseException {
        for (Part.Type type : types) {
            if (type == part.getType()) {
                return;
            }
        }
        throw new ParseException(format("Exepected [%s] but found %s at %s", Arrays.asList(types), part.getType(), part));
    }

    private void next() throws ParseException {
        idx++;
        if (idx >= parts.size()) {
            throw new ParseException("Parsed past end of list");
        }
        part = parts.get(idx);
    }

    private boolean hasNext() {
        return (idx < (parts.size() - 1));
    }

    private List<Part> unroll(Part head) {
        List<Part> res = new ArrayList();
        Part p = head;
        while (p != null) {
            res.add(p);
            p = p.right();
        }
        return res;
    }
}

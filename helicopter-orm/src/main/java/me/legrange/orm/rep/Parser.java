package me.legrange.orm.rep;

import static java.lang.String.format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import me.legrange.orm.OrmException;
import me.legrange.orm.impl.ContinuationPart;
import me.legrange.orm.impl.ExpressionContinuationPart;
import me.legrange.orm.impl.FieldPart;
import me.legrange.orm.impl.ListExpressionPart;
import me.legrange.orm.impl.Part;
import me.legrange.orm.impl.SelectPart;
import me.legrange.orm.impl.ValueExpressionPart;

/**
 *
 * @author gideon
 */
public class Parser {

    private List<Part> parts;
    private Part part;
    private int idx = -1;
    private final Stack<List<Part>> partsStack = new Stack();
    private final Stack<Integer> idxStack = new Stack();

    public Parser(List<Part> parts) {
        this.parts = new ArrayList(parts);
    }

    public Query parse() throws OrmException {
        next();
        expect(Part.Type.SELECT);
        Query query = new Query(((SelectPart) part).getReturnTable());
        while (hasNext()) {
            next();
            if (part.getType() == Part.Type.WHERE) {
                where();
            }
            if (part.getType() == Part.Type.JOIN) {
                join();
            }
            if (part.getType() == Part.Type.ORDER) {
                order();
            }

        }
        return query;
    }

    private void where() throws OrmException {
        expect(Part.Type.WHERE);
        Part expr = ((ContinuationPart) part).getExpression();
        push(unroll(expr));
        expression();
        pop();
    }

    private void join() {

    }

    private void order() {

    }

    private Criteria expression() throws ParseException, OrmException {
        expect(Part.Type.FIELD, Part.Type.NESTED_AND, Part.Type.NESTED_OR);
        FieldPart fieldPart = ((FieldPart) part);
        next();
        expect(Part.Type.LIST_EXPRESSION, Part.Type.VALUE_EXPRESSION);
        Criteria crit;

        switch (part.getType()) {
            case VALUE_EXPRESSION:
                crit = new ValueCriteria(fieldPart.getThis(), valueExpression());
                break;
            case LIST_EXPRESSION:
                crit = new ListCriteria(fieldPart.getThis(), listExpression());
                break;
            default:
                throw new ParseException("");
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

    private Object valueExpression() throws ParseException {
        expect(Part.Type.VALUE_EXPRESSION);
        return ((ValueExpressionPart) part).getValue();
    }

    private List listExpression() throws ParseException {
        expect(Part.Type.LIST_EXPRESSION);
        return ((ListExpressionPart) part).getValues();
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
        System.out.printf("part %s => %s\n", part.getType(), part.toString());
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

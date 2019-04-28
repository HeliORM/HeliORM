package me.legrange.orm.impl;

import static java.lang.String.format;
import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.OrmException;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public class ExpressionContinuationPart<T extends Table<O>, O> extends Part<T, O, T, O> implements ExpressionContinuation<T, O> {

    private final Type type;
    private final ExpressionContinuation expression;

    public ExpressionContinuationPart(Part left, Type type, ExpressionContinuation expr) {
        super(left);
        this.type = type;
        this.expression = expr;
        if (expr instanceof Part) {
            Part part = (Part) expr;
            Part head = ((Part) expr).head();
//            if ((head.left() == null) && (head instanceof FieldPart)) {
//                ((FieldPart) head).setLeft(this);
//            }
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public ExpressionContinuation<T, O> and(ExpressionContinuation<T, O> expr) {
        return new ExpressionContinuationPart(this, Type.NESTED_AND, expr);
    }

    @Override
    public ExpressionContinuation<T, O> or(ExpressionContinuation<T, O> expr) {
        return new ExpressionContinuationPart(this, Type.NESTED_OR, expr);
    }

    public Part getExpression() throws OrmException {
        Part part = (Part) expression;
        switch (part.getType()) {
            case VALUE_EXPRESSION:
            case LIST_EXPRESSION:
                return (ExpressionPart) part;
            case AND:
            case OR:
                return ((ContinuationPart) part).getExpression();
            default:
                throw new OrmException(format("Unexpected part of type %s as expression. BUG?", part.getType()));
        }
    }

    @Override
    public String toString() {
        return getType().name();
    }
}

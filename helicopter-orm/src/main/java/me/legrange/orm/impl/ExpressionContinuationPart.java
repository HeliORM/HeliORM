package me.legrange.orm.impl;

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
    private final Part expression;

    public ExpressionContinuationPart(Part left, Type type, ExpressionContinuation expr) {
        super(left);
        this.type = type;
        expression = ((Part) expr).head();
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
        return expression;
    }

    @Override
    public String toString() {
        return getType().name();
    }
}

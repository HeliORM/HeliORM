package net.legrange.orm.impl;

import net.legrange.orm.ExpressionContinuation;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 * @param <C>
 */
public abstract class ExpressionPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements ExpressionContinuation<T, O> {

    public ExpressionPart(Part left) {
        super(left);
    }

    @Override
    public ExpressionContinuation<T, O> and(ExpressionContinuation<T, O> expr) {
        return new ExpressionContinuationPart(this, Type.NESTED_AND, expr);
    }

    @Override
    public ExpressionContinuation<T, O> or(ExpressionContinuation<T, O> expr) {
        return new ExpressionContinuationPart(this, Type.NESTED_OR, expr);
    }

}

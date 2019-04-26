package me.legrange.orm.impl;

import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class ContinuationExpressionPart<T extends Table<O>, O> extends Part<T, O, T, O> implements ExpressionContinuation<T, O> {

    private final Type type;

    public ContinuationExpressionPart(Part left, Type type, ExpressionContinuation expr) {
        super(left);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public ExpressionContinuation<T, O> and(ExpressionContinuation<T, O> expr) {
        return new ContinuationExpressionPart(this, Type.AND, expr);
    }

    @Override
    public ExpressionContinuation<T, O> or(ExpressionContinuation<T, O> expr) {
        return new ContinuationExpressionPart(this, Type.OR, expr);
    }

}

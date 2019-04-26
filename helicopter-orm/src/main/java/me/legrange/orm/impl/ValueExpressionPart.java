package me.legrange.orm.impl;

import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class ValueExpressionPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements ExpressionContinuation<T, O> {

    private final Operator operator;
    private final C value;

    public enum Operator {
        EQ, NOT_EQ, LT, LE, GT, GE, LIKE, NOT_LIKE;
    }

    public ValueExpressionPart(Part left, Operator op, C value) {
        super(left);
        this.operator = op;
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.VALUE_EXPRESSION;
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

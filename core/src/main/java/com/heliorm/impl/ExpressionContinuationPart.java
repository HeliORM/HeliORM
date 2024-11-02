package com.heliorm.impl;

import com.heliorm.def.Continuation;

/**
 * @param <O> Type of POJO
 * @author gideon
 */
public final class ExpressionContinuationPart<O> implements Continuation<O> {

    public enum Type {
        AND, OR
    }

    private final Type type;
    private final ExpressionPart<O, ?> expression;

    public ExpressionContinuationPart(Type type, Continuation<O> expr) {
        expression = (ExpressionPart<O,?>) expr;
        this.type = type;
    }

    @Override
    public Continuation<O> and(Continuation<O> expr) {
        return new ExpressionContinuationPart<>(Type.AND, expr);
    }

    @Override
    public Continuation<O> or(Continuation<O> expr) {
        return new ExpressionContinuationPart<>(Type.OR, expr);
    }

    public ExpressionPart<O, ?> getExpression() {
        return expression;
    }

    public Type getType() {
        return type;
    }

}


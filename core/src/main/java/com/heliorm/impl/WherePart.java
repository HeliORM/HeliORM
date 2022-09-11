package com.heliorm.impl;

import com.heliorm.def.Continuation;
import com.heliorm.def.Where;

import java.util.LinkedList;
import java.util.List;

public class WherePart<O> implements Where<O> {

    private final ExpressionPart<O, ?> expression;
    private final List<ExpressionContinuationPart<O>> continuations = new LinkedList<>();

    public WherePart(Continuation<O> expr) {
        this.expression = (ExpressionPart<O, ?>) expr;
    }

    public ExpressionPart<O, ?> getExpression() {
        return expression;
    }

    public List<ExpressionContinuationPart<O>> getContinuations() {
        return continuations;
    }

    @Override
    public Where<O> and(Continuation<O> cont) {
        continuations.add(new ExpressionContinuationPart<>(ExpressionContinuationPart.Type.AND, cont));
        return this;
    }

    @Override
    public Where<O> or(Continuation<O> cont) {
        continuations.add(new ExpressionContinuationPart<>(ExpressionContinuationPart.Type.OR, cont));
        return this;
    }
}

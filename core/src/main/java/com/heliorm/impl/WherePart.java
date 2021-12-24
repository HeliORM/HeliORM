package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Where;
import com.heliorm.def.Continuation;

import java.util.LinkedList;
import java.util.List;

public class WherePart<T extends Table<O>, O> implements Where<T,O>  {

    private final ExpressionPart<T,O,?> expression;
    private final List<ExpressionContinuationPart<T,O>> continuations = new LinkedList<>();

    public WherePart(Continuation<T, O> expr) {
        this.expression = (ExpressionPart<T, O, ?>) expr;
    }

    public ExpressionPart<T, O,?> getExpression() {
        return expression;
    }

    public List<ExpressionContinuationPart<T, O>> getContinuations() {
        return continuations;
    }

    @Override
    public Where<T, O> and(Continuation<T, O> cont) {
        continuations.add(new ExpressionContinuationPart<>(ExpressionContinuationPart.Type.AND, cont));
        return this;
    }

    @Override
    public Where<T, O> or(Continuation<T, O> cont) {
        continuations.add(new ExpressionContinuationPart<>(ExpressionContinuationPart.Type.OR, cont));
        return this;
    }
}

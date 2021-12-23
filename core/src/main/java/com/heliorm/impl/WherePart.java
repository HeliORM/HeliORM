package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.Where;
import com.heliorm.def.Continuation;

import java.util.LinkedList;
import java.util.List;

public class WherePart<T extends Table<O>, O> implements Where<T,O>  {

    private final ExpressionContinuationPart<T,O> expression;
    private final List<ExpressionContinuationPart<T,O>> continuations = new LinkedList<>();

    public WherePart(Continuation<T, O> expr) {
        this.expression = (ExpressionContinuationPart<T, O>) expr;
    }

    public ExpressionContinuationPart<T, O> getExpression() {
        return expression;
    }

    @Override
    public Where<T, O> and(Continuation<T, O> cont) {
        continuations.add((ExpressionContinuationPart<T, O>) cont);
        return this;
    }

    @Override
    public Where<T, O> or(Continuation<T, O> cont) {
        continuations.add((ExpressionContinuationPart<T, O>) cont);
        return this;
    }
}

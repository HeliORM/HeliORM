package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Continuation;

/**
 *
 * @author gideon
 * @param <T> Type of table
 * @param <O> Type of POJO
 */
public class ExpressionContinuationPart<T extends Table<O>, O>  implements Continuation<T, O> {

    public enum Type {
        WHERE, AND, OR;
    }

    private final Type type;
    private final ExpressionPart expression;

    public ExpressionContinuationPart(Type type, Continuation expr) {
        expression =  ((ExpressionContinuationPart)expr).getExpression();
        this.type = type;
    }

    @Override
    public Continuation<T, O> and(Continuation<T, O> expr) {
        return new ExpressionContinuationPart(Type.AND, expr);
    }

    @Override
    public Continuation<T, O> or(Continuation<T, O> expr) {
        return new ExpressionContinuationPart(Type.OR, expr);
    }

    public ExpressionPart getExpression() {
        return expression;
    }

    public Type getType() {
        return type;
    }
}


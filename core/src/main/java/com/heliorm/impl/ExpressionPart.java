package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.def.Continuation;

import java.util.LinkedList;
import java.util.List;

/**
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public abstract class ExpressionPart<O, C> implements Continuation<O> {

    public enum Type {
        VALUE_EXPRESSION,
        LIST_EXPRESSION, IS_EXPRESSION
    }

    private final Type type;
    private final Field<O, C> field;
    private final List<ExpressionContinuationPart<O>> continuations = new LinkedList<>();
    public ExpressionPart(Type type, Field<O, C> field) {
        this.type = type;
        this.field = field;
    }

    @Override
    public Continuation<O> and(Continuation<O> expr) {
         continuations.add(new ExpressionContinuationPart<>(ExpressionContinuationPart.Type.AND, expr));
         return this;
    }

    @Override
    public Continuation<O> or(Continuation<O> expr) {
        continuations.add( new ExpressionContinuationPart<>(ExpressionContinuationPart.Type.OR, expr));
        return this;
    }

    public final Type getType() {
        return type;
    }

    public final Field<O, C> getField() {
        return field;
    }

    public List<ExpressionContinuationPart<O>> getContinuations() {
        return continuations;
    }

}

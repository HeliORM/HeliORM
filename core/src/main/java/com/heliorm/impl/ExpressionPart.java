package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.def.Continuation;

/**
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public abstract class ExpressionPart<O, C> implements Continuation<O> {


    private final Type type;
    private final Field<O, C> field;
    public ExpressionPart(Type type, Field<O, C> field) {
        this.type = type;
        this.field = field;
    }

    @Override
    public Continuation<O> and(Continuation<O> expr) {
        return new ExpressionContinuationPart(ExpressionContinuationPart.Type.AND, expr);
    }

    @Override
    public Continuation<O> or(Continuation<O> expr) {
        return new ExpressionContinuationPart(ExpressionContinuationPart.Type.OR, expr);
    }

    public final Type getType() {
        return type;
    }

    public final Field<O, C> getField() {
        return field;
    }

    public enum Type {
        VALUE_EXPRESSION,
        LIST_EXPRESSION, IS_EXPRESSION
    }
}

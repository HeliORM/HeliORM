package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.Field;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public abstract class ExpressionPart<T extends Table<O>, O, C> implements Continuation<T, O> {


    public enum Type {
        VALUE_EXPRESSION,
        LIST_EXPRESSION, IS_EXPRESSION
    }

    private final Type type;
    private final Field<T,O,C> field;

    public ExpressionPart(Type type, Field<T,O,C> field) {
        this.type = type;
        this.field = field;
    }

    @Override
    public Continuation<T, O> and(Continuation<T, O> expr) {
        return new ExpressionContinuationPart(ExpressionContinuationPart.Type.AND, expr);
    }

    @Override
    public Continuation<T, O> or(Continuation<T, O> expr) {
        return new ExpressionContinuationPart(ExpressionContinuationPart.Type.OR, expr);
    }

    public final Type getType() {
        return type;
    }

    public final Field<T, O, C> getField() {
        return field;
    }
}

package com.heliorm.impl;

import com.heliorm.def.ExpressionContinuation;
import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 */
public abstract class ExpressionPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements ExpressionContinuation<T, O> {

    public ExpressionPart(Part left) {
        super(left);
    }

    @Override
    public ExpressionContinuation<T, O> and(ExpressionContinuation<T, O> expr) {
        return new ExpressionContinuationPart(this, Type.NESTED_AND, expr);
    }

    @Override
    public ExpressionContinuation<T, O> or(ExpressionContinuation<T, O> expr) {
        return new ExpressionContinuationPart(this, Type.NESTED_OR, expr);
    }

}

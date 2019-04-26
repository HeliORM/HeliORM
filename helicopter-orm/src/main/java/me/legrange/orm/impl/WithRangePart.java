package me.legrange.orm.impl;

import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.Table;
import me.legrange.orm.WithRange;

/**
 *
 * @author gideon
 */
public interface WithRangePart<T extends Table<O>, O, C> extends WithRange<T, O, C> {

    Part<T, O, T, O> getThis();

    @Override
    public default ExpressionContinuation<T, O> lt(C value) {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public default ExpressionContinuation<T, O> le(C value) {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public default ExpressionContinuation<T, O> gt(C value) {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public default ExpressionContinuation<T, O> ge(C value) {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
    }

}

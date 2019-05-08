package me.legrange.orm.impl;

import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.OrmException;
import me.legrange.orm.Table;
import me.legrange.orm.WithRange;

/**
 *
 * @author gideon
 */
public interface WithRangePart<T extends Table<O>, O, C> extends WithRange<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    public default ExpressionContinuation<T, O> lt(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.LT, value);
    }

    @Override
    public default ExpressionContinuation<T, O> le(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.LE, value);
    }

    @Override
    public default ExpressionContinuation<T, O> gt(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.GT, value);
    }

    @Override
    public default ExpressionContinuation<T, O> ge(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.GE, value);
    }

}
package me.legrange.orm.impl;

import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.OrmException;
import me.legrange.orm.Table;
import me.legrange.orm.WithLike;

/**
 *
 * @author gideon
 */
public interface WithLikePart<T extends Table<O>, O, C> extends WithLike<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    public default ExpressionContinuation<T, O> like(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.LIKE, value);
    }

    @Override
    public default ExpressionContinuation<T, O> notLike(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_LIKE, value);
    }

}

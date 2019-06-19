package net.legrange.orm.impl;

import java.util.Arrays;
import java.util.List;
import net.legrange.orm.ExpressionContinuation;
import net.legrange.orm.OrmException;
import net.legrange.orm.Table;
import net.legrange.orm.WithIn;

/**
 *
 * @author gideon
 */
public interface WithInPart<T extends Table<O>, O, C> extends WithIn<T, O, C> {

    Part<T, O, T, O> getThis() throws OrmException;

    @Override
    public default ExpressionContinuation<T, O> in(List<C> values) throws OrmException {
        return new ListExpressionPart(getThis(), ListExpressionPart.Operator.IN, values);
    }

    @Override
    public default ExpressionContinuation<T, O> notIn(List<C> values) throws OrmException {
        return new ListExpressionPart(getThis(), ListExpressionPart.Operator.NOT_IN, values);
    }

    @Override
    public default ExpressionContinuation<T, O> in(C... values) throws OrmException {
        return new ListExpressionPart(getThis(), ListExpressionPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public default ExpressionContinuation<T, O> notIn(C... values) throws OrmException {
        return new ListExpressionPart(getThis(), ListExpressionPart.Operator.NOT_IN, Arrays.asList(values));
    }
}

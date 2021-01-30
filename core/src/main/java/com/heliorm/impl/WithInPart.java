package com.heliorm.impl;

import java.util.Arrays;
import java.util.List;

import com.heliorm.def.ExpressionContinuation;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.WithIn;

/**
 *
 * @author gideon
 */
public interface WithInPart<T extends Table<O>, O, C> extends WithIn<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

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

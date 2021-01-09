package com.heliorm.impl;

import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.WithEquals;
import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public interface WithEqualsPart<T extends Table<O>, O, C> extends WithEquals<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    public default ExpressionContinuation<T, O> eq(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.EQ, value);
    }

    @Override
    public default ExpressionContinuation<T, O> notEq(C value) throws OrmException {
        return new ValueExpressionPart(getThis(), ValueExpressionPart.Operator.NOT_EQ, value);
    }

}

package com.heliorm.impl;

import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.WithIs;
import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public interface WithIsPart<T extends Table<O>, O, C> extends WithIs<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    ExpressionContinuation<T, O> isNull() throws OrmException;

    @Override
    ExpressionContinuation<T, O> isNotNull() throws OrmException;
}

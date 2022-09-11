package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithIs;

/**
 * @author gideon
 */
public interface WithIsPart<O, C> extends WithIs<O> {

    FieldPart<O, C> getThis() throws OrmException;

    @Override
    Continuation<O> isNull() throws OrmException;

    @Override
    Continuation<O> isNotNull() throws OrmException;
}

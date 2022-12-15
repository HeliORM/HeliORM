package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithEquals;

/**
 * @author gideon
 */
public interface WithEqualsPart<O, C> extends WithEquals<O, C> {

    FieldPart<O, C> getThis() throws OrmException;

    @Override
    Continuation< O> eq(C value) throws OrmException;

    @Override
    Continuation<O> notEq(C value) throws OrmException;

}

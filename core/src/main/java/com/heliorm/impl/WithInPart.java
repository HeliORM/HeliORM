package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithIn;

import java.util.List;

/**
 * @author gideon
 */
public interface WithInPart<O, C> extends WithIn<O, C> {

    FieldPart<O, C> getThis() throws OrmException;

    @Override
    Continuation<O> in(List<C> values) throws OrmException;

    @Override
    Continuation<O> notIn(List<C> values) throws OrmException;

    @Override
    Continuation<O> in(C... values) throws OrmException;

    @Override
    Continuation<O> notIn(C... values) throws OrmException;

}

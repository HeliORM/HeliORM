package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithRange;

/**
 * @author gideon
 */
public interface WithRangePart< O, C> extends WithRange< O, C> {

    FieldPart<O, C> getThis() throws OrmException;

    @Override
    Continuation<O> lt(C value) throws OrmException;

    @Override
    Continuation<O> le(C value) throws OrmException;

    @Override
    Continuation<O> gt(C value) throws OrmException;

    @Override
    Continuation<O> ge(C value) throws OrmException;

}

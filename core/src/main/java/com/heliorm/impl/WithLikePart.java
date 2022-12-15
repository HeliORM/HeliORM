package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithLike;

/**
 * @author gideon
 */
public interface WithLikePart< O, C> extends WithLike< O, C> {

    FieldPart<O, C> getThis() throws OrmException;

    @Override
    Continuation<O> like(C value) throws OrmException;

    @Override
    Continuation<O> notLike(C value) throws OrmException;

}

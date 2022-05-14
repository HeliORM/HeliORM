package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithLike;

/**
 * @author gideon
 */
public interface WithLikePart<T extends Table<O>, O, C> extends WithLike<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    Continuation<T, O> like(C value) throws OrmException;

    @Override
    Continuation<T, O> notLike(C value) throws OrmException;

}

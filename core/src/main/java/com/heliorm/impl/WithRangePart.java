package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithRange;

/**
 * @author gideon
 */
public interface WithRangePart<T extends Table<O>, O, C> extends WithRange<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    Continuation<T, O> lt(C value) throws OrmException;

    @Override
    Continuation<T, O> le(C value) throws OrmException;

    @Override
    Continuation<T, O> gt(C value) throws OrmException;

    @Override
    Continuation<T, O> ge(C value) throws OrmException;

}

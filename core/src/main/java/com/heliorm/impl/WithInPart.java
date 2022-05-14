package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithIn;

import java.util.List;

/**
 * @author gideon
 */
public interface WithInPart<T extends Table<O>, O, C> extends WithIn<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    Continuation<T, O> in(List<C> values) throws OrmException;

    @Override
    Continuation<T, O> notIn(List<C> values) throws OrmException;

    @Override
    Continuation<T, O> in(C... values) throws OrmException;

    @Override
    Continuation<T, O> notIn(C... values) throws OrmException;

}

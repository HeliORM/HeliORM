package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.WithIs;

/**
 * @author gideon
 */
public interface WithIsPart<T extends Table<O>, O, C> extends WithIs<T, O, C> {

    FieldPart<T, O, C> getThis() throws OrmException;

    @Override
    Continuation<T, O> isNull() throws OrmException;

    @Override
    Continuation<T, O> isNotNull() throws OrmException;
}

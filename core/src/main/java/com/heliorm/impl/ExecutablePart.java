package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Executable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author gideon
 */
abstract class ExecutablePart<T extends Table<O>, O> extends Part<T, O, T, O>
        implements Executable<T, O> {

    public ExecutablePart(Type type, Part left) {
        super(type, left);
    }

    @Override
    public List<O> list() throws OrmException {
        return getSelector().list(this);
    }

    @Override
    public Stream<O> stream() throws OrmException {
        return getSelector().stream(this);
    }

    @Override
    public O one() throws OrmException {
        return getSelector().one(this);
    }

    @Override
    public Optional<O> optional() throws OrmException {
        return getSelector().optional(this);
    }

}

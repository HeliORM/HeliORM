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
abstract class ExecutablePart<DT extends Table<DO>, DO> extends Part<DT, DO, DT, DO>
        implements Executable<DT, DO> {

    public ExecutablePart(Type type, Part left) {
        super(type, left);
    }

    @Override
    public List<DO> list() throws OrmException {
        return getSelector().list(this);
    }

    @Override
    public Stream<DO> stream() throws OrmException {
        return getSelector().stream(this);
    }

    @Override
    public DO one() throws OrmException {
        return getSelector().one(this);
    }

    @Override
    public Optional<DO> optional() throws OrmException {
        return getSelector().optional(this);
    }

}

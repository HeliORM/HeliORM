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
public abstract class ExecutablePart<DT extends Table<DO>, DO> implements Executable<DO> {

    private final Selector selector;
    public ExecutablePart(Selector selector) {
        this.selector = selector;
    }

    public abstract SelectPart<DT,DO> getSelect();

    public abstract List<OrderPart<DT,DO>> getOrder();

    public abstract LimitPart<DO> getLimit();

    @Override
    public List<DO> list() throws OrmException {
        return selector.list(getSelect());
    }

    @Override
    public Stream<DO> stream() throws OrmException {
        return selector.stream(getSelect());
    }

    @Override
    public DO one() throws OrmException {
        return selector.one(getSelect());
    }

    @Override
    public Optional<DO> optional() throws OrmException {
        return selector.optional(getSelect());
    }

}

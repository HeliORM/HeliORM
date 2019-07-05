package net.legrange.orm.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.legrange.orm.OrmException;
import net.legrange.orm.Table;
import net.legrange.orm.def.Executable;

/**
 *
 * @author gideon
 */
abstract class ExecutablePart<T extends Table<O>, O> extends Part<T, O, T, O>
        implements Executable<T, O> {

    public ExecutablePart(Part left) {
        super(left);
    }

    @Override
    public List<O> list() throws OrmException {
        return getOrm().list(this);
    }

    @Override
    public Stream<O> stream() throws OrmException {
        return getOrm().stream(this);
    }

    @Override
    public O one() throws OrmException {
        return getOrm().one(this);
    }

    @Override
    public Optional<O> oneOrNone() throws OrmException {
        return getOrm().oneOrNone(this);
    }

}

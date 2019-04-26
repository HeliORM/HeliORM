package me.legrange.orm.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.legrange.orm.Executable;
import me.legrange.orm.OrmException;
import me.legrange.orm.Table;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public O one() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<O> oneOrNone() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

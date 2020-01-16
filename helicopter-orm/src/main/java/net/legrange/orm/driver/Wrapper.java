package net.legrange.orm.driver;

import net.legrange.orm.OrmException;
import net.legrange.orm.PojoOperations;
import net.legrange.orm.Table;
import net.legrange.orm.UncaughtOrmException;
import net.legrange.orm.def.Field;

import java.util.Optional;

/**
 * @author gideon
 */
class Wrapper<O> implements Comparable<Wrapper<O>> {

    private final PojoOperations pops;
    private final Table<O> table;
    private final O pojo;

    public Wrapper(PojoOperations pops, Table<O> table, O pojo) {
        this.pops = pops;
        this.table = table;
        this.pojo = pojo;
    }

    public O getPojo() {
        return pojo;
    }

    @Override
    public int compareTo(Wrapper<O> w) {
        if (table.equals(w.table)) {
            Optional<Field> primaryKey = table.getPrimaryKey();
            if (primaryKey.isPresent()) {
                try {
                    return pops.compareTo(pojo, w.getPojo(), primaryKey.get());
                } catch (OrmException ex) {
                    throw new UncaughtOrmException(ex.getMessage(), ex);
                }
            }
            if (pojo instanceof Comparable) {
                return ((Comparable) pojo).compareTo(w.getPojo());
            }
        }
        return table.getSqlTable().compareTo(w.table.getSqlTable());

    }

    @Override
    public int hashCode() {
        Optional<Field> primaryKey = table.getPrimaryKey();
        if (primaryKey.isPresent()) {
            try {
                return pops.getValue(pojo, primaryKey.get()).hashCode();
            } catch (OrmException ex) {
                throw new UncaughtOrmException(ex.getMessage(), ex);
            }
        }
        return pojo.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (Wrapper.class != obj.getClass()) {
            return false;
        }
        return compareTo((Wrapper<O>) obj) == 0;
    }

}

package com.heliorm.sql;

import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;

import java.util.Optional;

/**
 * A comparable that compares two POJOs taking into account the metadata
 * available from a Table
 *
 * @author gideon
 */
final class PojoCompare<O> implements Comparable<PojoCompare<O>> {

    private final PojoOperations pops;
    private final Table<O> table;
    private final O pojo;

    public PojoCompare(PojoOperations pops, Table<O> table, O pojo) {
        this.pops = pops;
        this.table = table;
        this.pojo = pojo;
    }

    public O getPojo() {
        return pojo;
    }

    public Table<O> getTable() {
        return table;
    }

    @Override
    public int compareTo(PojoCompare<O> w) {
        if (table.equals(w.table)) {
            Optional<Field<O, ?>> primaryKey = table.getPrimaryKey();
            if (primaryKey.isPresent()) {
                try {
                    return pops.compareTo(pojo, w.getPojo(), primaryKey.get());
                } catch (OrmException ex) {
                    throw new UncaughtOrmException(ex.getMessage(), ex);
                }
            }
            if (pojo instanceof Comparable) {
                return ((Comparable<O>) pojo).compareTo(w.getPojo());
            }
        }
        return table.getSqlTable().compareTo(w.table.getSqlTable());

    }

    @Override
    public int hashCode() {
        Optional<Field<O,?>> primaryKey = table.getPrimaryKey();
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
        if (PojoCompare.class != obj.getClass()) {
            return false;
        }
        return compareTo((PojoCompare<O>) obj) == 0;
    }

    int compareTo(PojoCompare<O> w, Field<O, ?> field) throws UncaughtOrmException {
        try {
            return pops.compareTo(pojo, w.getPojo(), field);
        } catch (OrmException ex) {
            throw new UncaughtOrmException(ex.getMessage(), ex);
        }
    }

}

package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;
import com.heliorm.def.Field;

import java.util.Optional;

/**
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

    @Override
    public int compareTo(PojoCompare<O> w) {
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
        if (PojoCompare.class != obj.getClass()) {
            return false;
        }
        return compareTo((PojoCompare<O>) obj) == 0;
    }

    int compareTo(PojoCompare<O> w, Field<?, O, ?> field) throws UncaughtOrmException {
        try {
            return pops.compareTo(pojo, w.getPojo(), field);
        }
        catch (OrmException ex) {
            throw new UncaughtOrmException(ex.getMessage(), ex);
        }
    }

}

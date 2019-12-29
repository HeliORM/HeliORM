package net.legrange.orm;

import net.legrange.orm.OrmException;

public interface OrmTransaction extends AutoCloseable {

    void commit() throws OrmException;

    void rollback() throws OrmException;

    @Override
    void close() throws OrmException;
}

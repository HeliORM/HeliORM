package net.legrange.orm;

import net.legrange.orm.OrmException;

/** A transaction returned by the ORM. */
public interface OrmTransaction extends AutoCloseable {

    /** Commit changes and close the transaction.
     *
     * @throws OrmException
     */
    void commit() throws OrmException;

    /** Rollback changes and close the transaction.
     *
     * @throws OrmException
     */
    void rollback() throws OrmException;

    @Override
    void close() throws OrmException;
}

package com.heliorm;

/**
 * A transaction returned by the ORM.
 */
public interface OrmTransaction extends AutoCloseable {

    /**
     * Commit changes and close the transaction.
     *
     */
    void commit() throws OrmException;

    /**
     * Rollback changes and close the transaction.
     *
     */
    void rollback() throws OrmException;

    @Override
    void close() throws OrmException;
}

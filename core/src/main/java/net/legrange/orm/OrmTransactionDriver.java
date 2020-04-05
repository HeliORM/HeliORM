package net.legrange.orm;

/**
 * A driver that supports transactions must implement this to allow the ORM to use it's transaction support.
 */
public interface OrmTransactionDriver {

    /**
     * Open a transaction.
     *
     * @return The transaction
     * @throws OrmException An error if a transaction could not be opened.
     */
    OrmTransaction openTransaction() throws OrmException;

    /**
     * Control whether the driver should rollback un-commited transactions when they are closed or raise an exception.
     *
     * @param rollback True for rollback, false for exception
     */
    void setRollbackOnUncommittedClose(boolean rollback);

}

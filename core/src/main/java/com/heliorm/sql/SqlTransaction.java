package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;
import com.heliorm.OrmTransactionException;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;

/** A SQL transaction
 *
 */
final class SqlTransaction implements OrmTransaction, AutoCloseable {

    private final SqlDriver driver;
    private final Connection connection;
    private boolean open;

    /** Create a new transactoion for the given driver.
     *
     * @param driver The driver to use
     * @throws OrmTransactionException
     */
    SqlTransaction(SqlDriver driver) throws OrmTransactionException {
        this.driver = driver;
        this.connection = driver.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new OrmTransactionException(format("Cannot disable SQL connection auto-commit (%s)", e.getMessage()));
        }
        this.open = true;
    }

    @Override
    public void commit() throws OrmException {
        try {
            if (!open) {
                throw new OrmTransactionException(format("Cannot commit an already comitted or rolled back SQL transaction"));
            }
            connection.commit();
            open = false;
        } catch (SQLException ex) {
            throw new OrmException(format("Error commiting transaction (%s)", ex.getMessage()));
        }
    }

    @Override
    public void rollback() throws OrmException {
        try {
            if (!open) {
                throw new OrmTransactionException(format("Cannot commit an already comitted or rolled back SQL transaction"));
            }
            connection.rollback();
            open = false;
        } catch (SQLException ex) {
            throw new OrmException(format("Error rolling back transaction (%s)", ex.getMessage()));
        }

    }

    @Override
    public void close() throws OrmException {
        if (open) {
            if (!driver.getRollbackOnUncommittedClose()) {
                open = false;
                throw new OrmException("Transaction is being auto-closed without committing or rolling back");
            }
            rollback();
        }
    }

    Connection getConnection() {
        return connection;
    }

    boolean isOpen() {
        return open;
    }

}

package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.OrmTransaction;
import com.heliorm.OrmTransactionException;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;

/**
 * A SQL transaction
 *
 * @author gideon
 */
final class SqlTransaction implements OrmTransaction, AutoCloseable {

    private final SqlDriver driver;
    private final Connection connection;
    private boolean open;

    /**
     * Create a new transaction for the given driver.
     *
     * @param driver     The driver for this transaction
     * @param connection the SQL Connection for this transaction
     */
    SqlTransaction(SqlDriver driver, Connection connection) throws OrmTransactionException {
        this.driver = driver;
        this.connection = connection;
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
                throw new OrmTransactionException("Cannot commit an already committed or rolled back SQL transaction");
            }
            connection.commit();
            open = false;
            connection.close();
        } catch (SQLException ex) {
            throw new OrmException(format("Error committing transaction (%s)", ex.getMessage()));
        }
    }

    @Override
    public void rollback() throws OrmException {
        try {
            if (!open) {
                throw new OrmTransactionException("Cannot commit an already committed or rolled back SQL transaction");
            }
            connection.rollback();
            open = false;
            connection.close();
        } catch (SQLException ex) {
            throw new OrmException(format("Error rolling back transaction (%s)", ex.getMessage()));
        }

    }

    @Override
    public void close() throws OrmException {
        if (open) {
            if (!driver.getRollbackOnUncommittedClose()) {
                open = false;
                try {
                    connection.close();
                }
                catch (SQLException ex) {
                    throw new OrmException(format("Transaction is being auto-closed without committing or rolling back (%s)", ex.getMessage()),ex);
                }
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

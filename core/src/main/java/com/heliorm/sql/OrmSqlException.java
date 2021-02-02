package com.heliorm.sql;

import com.heliorm.OrmException;

/**
 * Exception thrown when the ORM encounters a SQL error.
 *
 * @author gideon
 */
public final class OrmSqlException extends OrmException {

    public OrmSqlException(String message) {
        super(message);
    }

    public OrmSqlException(String message, Throwable cause) {
        super(message, cause);
    }

}

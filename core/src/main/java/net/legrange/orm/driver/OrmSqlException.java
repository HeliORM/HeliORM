package net.legrange.orm.driver;

import net.legrange.orm.OrmException;

/**
 * Exception thrown when the ORM encounters a SQL error.
 *
 * @author gideon
 */
public class OrmSqlException extends OrmException {

    OrmSqlException(String message) {
        super(message);
    }

    OrmSqlException(String message, Throwable cause) {
        super(message, cause);
    }

}

package net.legrange.orm;

import net.legrange.orm.OrmException;

import java.lang.annotation.Documented;

/** Thrown by the ORM if there is a problem with a transaction.
 *
 * @author gideon
 *
 */
public class OrmTransactionException extends OrmException {

    public OrmTransactionException(String message) {
        super(message);
    }

    public OrmTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

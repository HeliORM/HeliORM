package net.legrange.orm;

import net.legrange.orm.OrmException;

public class OrmTransactionException extends OrmException {

    public OrmTransactionException(String message) {
        super(message);
    }

    public OrmTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

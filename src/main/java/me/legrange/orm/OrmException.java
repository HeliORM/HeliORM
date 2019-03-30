package me.legrange.orm;

/**
 *
 * @author gideon
 */
public class OrmException extends Exception {

    public OrmException(String message) {
        super(message);
    }

    public OrmException(String message, Throwable cause) {
        super(message, cause);
    }

}

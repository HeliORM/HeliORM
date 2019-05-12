package me.legrange.orm;

/**
 *
 * @author gideon
 */
public class UncaughtOrmException extends RuntimeException {

    public UncaughtOrmException(String message) {
        super(message);
    }

    public UncaughtOrmException(String message, Throwable cause) {
        super(message, cause);
    }

}

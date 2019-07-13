package net.legrange.orm.mojo.annotated;

/**
 *
 * @author gideon
 */
public class UncaughtPojoException extends RuntimeException {

    public UncaughtPojoException(String message) {
        super(message);
    }

    public UncaughtPojoException(String message, Throwable cause) {
        super(message, cause);
    }

}

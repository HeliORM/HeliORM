package com.heliorm;

/**
 * Thrown by the ORM if an error occurs which cannot be thrown as a checked
 * exception. This is needed to support lambda and stream operations. Users of
 * the ORM are encouraged to catch and deal with this exception, but obviously
 * it cannot be enforced.
 *
 * @author gideon
 */
public class UncaughtOrmException extends RuntimeException {

    /**
     * Create a new exception with the given message.
     *
     * @param message The message
     */
    public UncaughtOrmException(String message) {
        super(message);
    }

    /**
     * Create a new exception with the given message and underlying cause.
     *
     * @param message The message
     * @param cause   The underlying error
     */
    public UncaughtOrmException(String message, Throwable cause) {
        super(message, cause);
    }

}

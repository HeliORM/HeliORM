package com.heliorm;

/**
 * Thrown by the ORM if an error occurs. This is a checked exception and where
 * it is thrown in a public interface, the user needs to catch it and do error
 * handling.
 *
 * @author gideon
 */
public class OrmException extends Exception {

    /**
     * Create a new exception with the given message.
     *
     * @param message The message
     */
    public OrmException(String message) {
        super(message);
    }

    /**
     * Create a new exception with the given message and underlying cause.
     *
     * @param message The message
     * @param cause   The underlying error
     */
    public OrmException(String message, Throwable cause) {
        super(message, cause);
    }

}

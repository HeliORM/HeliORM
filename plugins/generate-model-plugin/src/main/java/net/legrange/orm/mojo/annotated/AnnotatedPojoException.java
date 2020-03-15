package net.legrange.orm.mojo.annotated;

/**
 * Exception thrown when there is a fatal error in annotation processing
 *
 * @author gideon
 */
public class AnnotatedPojoException extends RuntimeException {

    public AnnotatedPojoException(String message) {
        super(message);
    }

    public AnnotatedPojoException(String message, Throwable cause) {
        super(message, cause);
    }

}

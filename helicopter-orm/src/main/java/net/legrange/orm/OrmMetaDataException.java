package net.legrange.orm;

/**
 * Exception thrown when the ORM encounters a SQL error.
 *
 * @author gideon
 */
public class OrmMetaDataException extends OrmException {

    public OrmMetaDataException(String message) {
        super(message);
    }

    public OrmMetaDataException(String message, Throwable cause) {
        super(message, cause);
    }

}

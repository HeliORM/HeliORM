package net.legrange.orm.rep;

import net.legrange.orm.OrmException;

/**
 *
 * @author gideon
 */
public class ParseException extends OrmException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
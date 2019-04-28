package me.legrange.orm.rep;

import me.legrange.orm.OrmException;

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

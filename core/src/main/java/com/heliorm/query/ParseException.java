package com.heliorm.query;

import com.heliorm.OrmException;

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

package com.heliorm.mojo;

import com.heliorm.OrmException;

/**
 * Exception thrown when the meta data generator encounters and error. error.
 *
 * @author gideon
 */
public class OrmMetaDataException extends OrmException {

    public OrmMetaDataException(String message) {
        super(message);
    }

}

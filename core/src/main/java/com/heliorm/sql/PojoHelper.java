package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.def.Field;

import java.time.Duration;
import java.time.Instant;

import static java.lang.String.format;

class PojoHelper {

    private final PojoOperations pops;

    public PojoHelper(PojoOperations pops) {
        this.pops = pops;
    }

    /**
     * Get a value from the given POJO for the given field as an object
     *
     * @param pojo  The POJO from which to read the object.
     * @param field The field to read.
     * @return The object value.
     * @throws OrmException
     */
    Object getValueFromPojo(Object pojo, Field field) throws OrmException {
        return pops.getValue(pojo, field);
    }


    /**
     * Get a string value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the string.
     * @param field The field to read.
     * @return The string value.
     * @throws OrmException
     */
     String getStringFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            throw new OrmException(format("Could not read String value for field type '%s'.", field.getFieldType()));
        }
        return (String) value;
    }


    /**
     * Get a date value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the date.
     * @param field The field to read.
     * @return The date value.
     * @throws OrmException
     */
     java.sql.Date getDateFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof java.util.Date)) {
            throw new OrmException(format("Could not read Date value for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
        }
        return new java.sql.Date(((java.util.Date) value).getTime());
    }

    /**
     * Get a timestamp value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the timestamp.
     * @param field The field to read.
     * @return The timestamp value.
     * @throws OrmException
     */
     java.sql.Timestamp getTimestampFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (value instanceof Instant) {
            return new java.sql.Timestamp(((Instant) value).toEpochMilli());
        }
        if (value instanceof java.util.Date) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }
        throw new OrmException(format("Could not read Instant value for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
    }

    /**
     * Get a duration value from the given POJO for the given field.
     *
     * @param pojo  The POJO from which to read the timestamp.
     * @param field The field to read.
     * @return The duration string value.
     * @throws OrmException
     */
     String getDurationFromPojo(Object pojo, Field field) throws OrmException {
        Object value = getValueFromPojo(pojo, field);
        if (value == null) {
            return null;
        }
        if (value instanceof Duration) {
            return ((Duration) value).toString();
        }
        throw new OrmException(format("Could not read Duration value for field '%s' with type '%s'.", field.getJavaName(), field.getFieldType()));
    }

}

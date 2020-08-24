package com.heliorm;

import sun.misc.Unsafe;

import static java.lang.String.format;

/**
 * An implementation of PojoOperations that uses the sun.misc.Unsafe API to
 * manipulate POJOs without the need for a lot of reflection or adherence to
 * specific conventions. This allows the user to write their POJOs the way they
 * want to reflect their application logic without caring about ceremony.
 * Default constructors and getters and setters are not required.
 * <p>
 * This is however using an internal Java API with an unclear future, so users
 * who worry about this can implement PojoOperations in different and safer
 * ways.
 *
 * @author gideon
 */
final class UnsafePojoOperations extends AbstractPojoOperations {

    private final Unsafe unsafe;


    UnsafePojoOperations() throws OrmException {
        unsafe = getUnsafe();
    }

    /**
     * Get the value for the given field from the given object and return either
     * a boolean or a Boolean object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getBoolean(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getBoolean(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a long or a Long object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getLong(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getLong(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * an int or an Integer object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getInteger(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getInt(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a short or a Short object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getShort(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getShort(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a byte or a Byte object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getByte(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getByte(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a double or a Double object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getDouble(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getDouble(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a float or a Float object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getFloat(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getFloat(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * an enum value.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    protected Object getEnum(Object pojo, java.lang.reflect.Field field) throws OrmException {
        Object value = getObject(pojo, field);
        if (value != null) {
            Class<?> valueClass = value.getClass();
            if (valueClass.isEnum()) {
                return ((Enum) value).name();
            }
            throw new OrmException(format("Cannot read Pojo enum field from data of type %s", valueClass.getSimpleName()));
        }
        return null;
    }

    /**
     * Convenience method to retrieve the value of a field from an object.
     *
     * @param pojo  The object to get data from
     * @param field The field for which to get the data
     * @return The data
     */
    protected Object getObject(Object pojo, java.lang.reflect.Field field) {
        return unsafe.getObject(pojo, unsafe.objectFieldOffset(field));
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * boolean value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setBoolean(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putBoolean(pojo, unsafe.objectFieldOffset(field), (boolean) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * long value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setLong(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putLong(pojo, unsafe.objectFieldOffset(field), (long) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * integer value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setInteger(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putInt(pojo, unsafe.objectFieldOffset(field), (int) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * short value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setShort(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putShort(pojo, unsafe.objectFieldOffset(field), (short) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * byte value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setByte(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putByte(pojo, unsafe.objectFieldOffset(field), (byte) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * double value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setDouble(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putDouble(pojo, unsafe.objectFieldOffset(field), (double) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * float value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setFloat(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putFloat(pojo, unsafe.objectFieldOffset(field), (float) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * enum value.
     *
     * @param pojo  The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    protected void setEnum(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (value != null) {
            Class<?> valueClass = value.getClass();
            if (valueClass.isEnum()) {
                unsafe.putObject(pojo, unsafe.objectFieldOffset(field), value);
            } else {
                if (!String.class.isAssignableFrom(valueClass)) {
                    throw new OrmException(format("Cannot update Pojo enum field from data of type %s", valueClass.getSimpleName()));
                }
                Class<?> fieldClass = field.getType();
                if (!fieldClass.isEnum()) {
                    throw new OrmException(format("Field '%s' on %s is supposed to be an emum type. BUG!", field.getName(), field.getDeclaringClass().getSimpleName()));
                }
                unsafe.putObject(pojo, unsafe.objectFieldOffset(field), Enum.valueOf((Class<Enum>) fieldClass, (String) value));
            }
        }
    }

    /**
     * Convenience method to set the value supplied for the given field on the
     * given object.
     *
     * @param pojo  The object on which to set the value
     * @param field The field for which to set the value
     * @param value The value
     */
    protected void setObject(Object pojo, java.lang.reflect.Field field, Object value) {
        unsafe.putObject(pojo, unsafe.objectFieldOffset(field), value);
    }

    @Override
    protected Object newPojoInstance(Class<?> type) throws OrmException {
        try {
            return unsafe.allocateInstance(type);
        } catch (InstantiationException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
    }

    /**
     * Get hold of the sun.misc.Unsafe object to use for object manipulation.
     *
     * @return The Unsafe object
     * @throws OrmException Thrown if there is an error while getting the Unsafe
     *                      object
     */
    private static Unsafe getUnsafe() throws OrmException {
        try {
            java.lang.reflect.Field f = Unsafe.class
                    .getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new OrmException(format("Error getting Unsafe (%s)", ex.getMessage()), ex);
        }
    }
}

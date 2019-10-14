package net.legrange.orm;

import static java.lang.String.format;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.legrange.orm.def.Field;
import sun.misc.Unsafe;

/**
 * An implementation of PojoOperations that uses the sun.misc.Unsafe API to
 * manipulate POJOs without the need for a lot of reflection or adherence to
 * specific conventions. This allows the user to write their POJOs the way they
 * want to reflect their application logic without caring about ceremony.
 * Default constructors and getters and setters are not required.
 *
 * This is however using an internal Java API with an unclear future, so users
 * who worry about this can implement PojoOperations in different and safer
 * ways.
 *
 * @author gideon
 */
class UnsafeFieldOperation implements PojoOperations {

    private final Unsafe unsafe;

    @Override
    public Object newPojoInstance(Table table) throws OrmException {
        Class clazz = table.getObjectClass();
        try {
            for (Constructor cons : clazz.getConstructors()) {
                if (cons.getParameterCount() == 0) {
                    return cons.newInstance(new Object[]{});
                }
            }
            return unsafe.allocateInstance(table.getObjectClass());
        } catch (InstantiationException ex) {
            throw new OrmException(ex.getMessage(), ex);
        } catch (SecurityException ex) {
            throw new OrmException(format("Security error creating instance of %s (%s)", clazz.getCanonicalName(), ex.getMessage()));
        } catch (IllegalAccessException ex) {
            throw new OrmException(format("Access error creating instance of %s (%s)", clazz.getCanonicalName(), ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            throw new OrmException(format("Argument error creating instance of %s (%s)", clazz.getCanonicalName(), ex.getMessage()));
        } catch (InvocationTargetException ex) {
            throw new OrmException(format("Error creating instance of %s (%s)", clazz.getCanonicalName(), ex.getMessage()));
        }
    }

    @Override
    public void setValue(Object pojo, Field field, Object value) throws OrmException {
        java.lang.reflect.Field refField = getDeclaredField(pojo.getClass(), field.getJavaName());
        switch (field.getFieldType()) {
            case LONG:
                setLong(pojo, refField, value);
                break;
            case INTEGER:
                setInteger(pojo, refField, value);
                break;
            case SHORT:
                setShort(pojo, refField, value);
                break;
            case BYTE:
                setByte(pojo, refField, value);
                break;
            case DOUBLE:
                setDouble(pojo, refField, value);
                break;
            case FLOAT:
                setFloat(pojo, refField, value);
                break;
            case BOOLEAN:
                setBoolean(pojo, refField, value);
                break;
            case ENUM:
                setEnum(pojo, refField, value);
                break;
            case DATE:
            case TIMESTAMP:
            case DURATION:
            case STRING:
                setObject(pojo, refField, value);
                break;
            default:
                throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    @Override
    public Object getValue(Object pojo, Field field) throws OrmException {
        java.lang.reflect.Field refField = getDeclaredField(pojo.getClass(), field.getJavaName());
        switch (field.getFieldType()) {
            case LONG:
                return getLong(pojo, refField);
            case INTEGER:
                return getInteger(pojo, refField);
            case SHORT:
                return getShort(pojo, refField);
            case BYTE:
                return getByte(pojo, refField);
            case DOUBLE:
                return getDouble(pojo, refField);
            case FLOAT:
                return getFloat(pojo, refField);
            case BOOLEAN:
                return getBoolean(pojo, refField);
            case ENUM:
                return getEnum(pojo, refField);
            case DATE:
            case TIMESTAMP:
            case DURATION:
            case STRING:
                return getObject(pojo, refField);
            default:
                throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    @Override
    public int compareTo(Object pojo1, Object pojo2, Field field) throws OrmException {
        switch (field.getFieldType()) {
            case LONG:
            case INTEGER:
            case SHORT:
            case BYTE:
            case DOUBLE:
            case FLOAT:
            case BOOLEAN:
            case ENUM:
            case STRING:
            case DATE:
            case TIMESTAMP:
            case DURATION:
                Object val1 = getValue(pojo1, field);
                Object val2 = getValue(pojo2, field);
                if (val1 instanceof Comparable) {
                    return ((Comparable) val1).compareTo((Comparable) val2);
                } else {
                    throw new OrmException(format("Non-comparable type %s for field %s", field.getJavaType(), field.getJavaName()));
                }
            default:
                throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }

    }

    UnsafeFieldOperation() throws OrmException {
        unsafe = getUnsafe();
    }

    /**
     * Get the value for the given field from the given object and return either
     * a boolean or a Boolean object.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getBoolean(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getBoolean(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a long or a Long object.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getLong(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getLong(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * an int or an Integer object.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getInteger(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getInt(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a short or a Short object.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getShort(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getShort(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a byte or a Byte object.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getByte(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getByte(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a double or a Double object.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getDouble(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getDouble(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * a float or a Float object.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getFloat(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getFloat(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    /**
     * Get the value for the given field from the given object and return either
     * an enum value.
     *
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The result
     */
    private Object getEnum(Object pojo, java.lang.reflect.Field field) throws OrmException {
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
     * @param pojo The object to get data from
     * @param field The field for which to get the data
     * @return The data
     */
    private Object getObject(Object pojo, java.lang.reflect.Field field) {
        return unsafe.getObject(pojo, unsafe.objectFieldOffset(field));
    }

    /**
     * Set the value for the given field on the given object to the supplied
     * boolean value.
     *
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setBoolean(Object pojo, java.lang.reflect.Field field, Object value) {
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
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setLong(Object pojo, java.lang.reflect.Field field, Object value) {
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
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setInteger(Object pojo, java.lang.reflect.Field field, Object value) {
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
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setShort(Object pojo, java.lang.reflect.Field field, Object value) {
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
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setByte(Object pojo, java.lang.reflect.Field field, Object value) {
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
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setDouble(Object pojo, java.lang.reflect.Field field, Object value) {
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
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setFloat(Object pojo, java.lang.reflect.Field field, Object value) {
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
     * @param pojo The object on which to set the data
     * @param field The field for which to set the data
     * @param value The value to set
     */
    private void setEnum(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
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

    /**
     * Convenience method to set the value supplied for the given field on the
     * given object.
     *
     * @param pojo The object on which to set the value
     * @param field The field for which to set the value
     * @param value The value
     */
    private void setObject(Object pojo, java.lang.reflect.Field field, Object value) {
        unsafe.putObject(pojo, unsafe.objectFieldOffset(field), value);
    }

    /**
     * Recursively find the reflected field or the given field name on the given
     * class.
     *
     * @param clazz The class
     * @param fieldName The field name to find
     * @return The field
     * @throws OrmException Thrown if the field can't be found or accessed
     */
    private java.lang.reflect.Field getDeclaredField(Class<?> clazz, String fieldName) throws OrmException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            Class<?> superClass = clazz.getSuperclass();
            if ((superClass != null) && (superClass != Object.class)) {
                java.lang.reflect.Field res = getDeclaredField(superClass, fieldName);
                if (res
                        == null) {
                    throw new OrmException(format("Could not find field '%s' on class '%s'", fieldName, clazz.getName()));
                }
                return res;
            }
        } catch (SecurityException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Get hold of the sun.misc.Unsafe object to use for object manipulation.
     *
     * @return The Unsafe object
     * @throws OrmException Thrown if there is an error while getting the Unsafe
     * object
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

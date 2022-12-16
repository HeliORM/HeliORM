package com.heliorm.sql;

import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import static java.lang.String.format;

/**
 * Abstract implementation for creating PojoOperations
 *
 * @author gideon
 */
abstract class AbstractPojoOperations implements PojoOperations {

    private final Map<Class<?>, Map<String, java.lang.reflect.Field>> fields = new WeakHashMap();

    protected AbstractPojoOperations() {
    }

    @Override
    public final Object newPojoInstance(Table table) throws OrmException {
        Class clazz = table.getObjectClass();
        try {
            for (Constructor cons : clazz.getConstructors()) {
                if (cons.getParameterCount() == 0) {
                    return cons.newInstance();
                }
            }
            return newPojoInstance(table.getObjectClass());
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
    public final void setValue(Object pojo, Field field, Object value) throws OrmException {
        if (field == null) {
            throw new OrmException("Null field type passed to setValue(). BUG!");
        }
        java.lang.reflect.Field refField = getDeclaredField(pojo.getClass(), field.getJavaName());
        if ((value == null) && refField.getType().isPrimitive()) {
            throw new OrmException(format("Null value for primitive %s field %s. BUG", refField.getType().getSimpleName(), field.getJavaName()));
        }
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
            case STRING:
            case SET:
            case LIST:
                setObject(pojo, refField, value);
                break;
            default:
                throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    @Override
    public final Object getValue(Object pojo, Field field) throws OrmException {
        if (field == null) {
            throw new OrmException("Null field type passed to getValue(). BUG!");
        }
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
            case STRING:
            case SET :
            case LIST:
                return getObject(pojo, refField);
            default:
                throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    protected abstract Object getByte(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getShort(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getInteger(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getLong(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getDouble(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getFloat(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getBoolean(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getEnum(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract Object getObject(Object pojo, java.lang.reflect.Field refField) throws OrmException;

    protected abstract void setByte(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setShort(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setInteger(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setLong(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setDouble(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setFloat(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setBoolean(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setEnum(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    protected abstract void setObject(Object pojo, java.lang.reflect.Field refField, Object value) throws OrmException;

    @Override
    public final int compareTo(Object pojo1, Object pojo2, Field field) throws OrmException {
        Object val1 = getValue(pojo1, field);
        Object val2 = getValue(pojo2, field);
        if (val1 == val2) return 0;
        if (val1 == null) return -1;
        if (val2 == null) return 1;
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
                if (val1 == val2) return 0;
                if (val1 == null) return -1;
                if (val2 == null) return 1;
                if (val1 instanceof Comparable) {
                    return ((Comparable) val1).compareTo(val2);
                } else {
                    throw new OrmException(format("Non-comparable type %s for field %s", field.getJavaType(), field.getJavaName()));
                }
            case SET :
            case LIST:
                if (val1 instanceof Collection) {
                    Collection c1 = (Collection) val1;
                    Collection c2 = (Collection) val2;
                    if (c1.size() < c2.size()) {
                        return  -1;
                    }
                    if (c1.size() > c2.size()) {
                        return 1;
                    }
                    return 0;
                }
            default:
                throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    protected abstract Object newPojoInstance(Class<?> type) throws OrmException;

    /**
     * Recursively find the reflected field or the given field name on the given
     * class.
     *
     * @param clazz     The class
     * @param fieldName The field name to find
     * @return The field
     * @throws OrmException Thrown if the field can't be found or accessed
     */
    private java.lang.reflect.Field getDeclaredField(final Class<?> clazz, final String fieldName) throws OrmException {
        Map<String, java.lang.reflect.Field> forClass = fields.computeIfAbsent(clazz, type -> new WeakHashMap<>());
        if (forClass.containsKey(fieldName)) {
            return forClass.get(fieldName);
        }
        java.lang.reflect.Field field = findDeclaredField(clazz, fieldName);
        forClass.put(fieldName, field);
        return field;
    }

    /**
     * Recursively find the reflected field or the given field name on the given
     * class.
     *
     * @param clazz     The class
     * @param fieldName The field name to find
     * @return The field
     * @throws OrmException Thrown if the field can't be found or accessed
     */
    private java.lang.reflect.Field findDeclaredField(final Class<?> clazz, final String fieldName) throws OrmException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            Class<?> superClass = clazz.getSuperclass();
            if ((superClass != null) && (superClass != Object.class)) {
                return getDeclaredField(superClass, fieldName);
            }
        } catch (SecurityException ex) {
            throw new OrmException(ex.getMessage(), ex);
        }
        throw new OrmException(format("Could not find field '%s' on class '%s'", fieldName, clazz.getName()));
    }


}

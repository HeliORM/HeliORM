package com.heliorm.sql;

import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

import static java.lang.String.format;

/**
 * Abstract implementation for creating PojoOperations
 *
 * @author gideon
 */
abstract class AbstractPojoOperations implements PojoOperations {

    private final Map<Class<?>, Map<String, java.lang.reflect.Field>> fields = new WeakHashMap<>();

    protected AbstractPojoOperations() {
    }

    @Override
    public final <O> O newPojoInstance(Table<O> table) throws OrmException {
        var clazz = table.getObjectClass();
        try {
            for (var cons : clazz.getConstructors()) {
                if (cons.getParameterCount() == 0) {
                    //noinspection unchecked
                    return (O)cons.newInstance();
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
    public final <O> void setValue(O pojo, Field<O,?> field, Object value) throws OrmException {
        if (field == null) {
            throw new OrmException("Null field type passed to setValue(). BUG!");
        }
        java.lang.reflect.Field refField = getDeclaredField(pojo.getClass(), field.getJavaName());
        if ((value == null) && refField.getType().isPrimitive()) {
            throw new OrmException(format("Null value for primitive %s field %s. BUG", refField.getType().getSimpleName(), field.getJavaName()));
        }
        switch (field.getFieldType()) {
            case LONG -> setLong(pojo, refField, value);
            case INTEGER -> setInteger(pojo, refField, value);
            case SHORT -> setShort(pojo, refField, value);
            case BYTE -> setByte(pojo, refField, value);
            case DOUBLE -> setDouble(pojo, refField, value);
            case FLOAT -> setFloat(pojo, refField, value);
            case BOOLEAN -> setBoolean(pojo, refField, value);
            case ENUM -> setEnum(pojo, refField, value);
            case DATE, INSTANT, LOCAL_DATE_TIME, STRING, BYTE_ARRAY -> setObject(pojo, refField, value);
            default -> throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    @Override
    public final <O> Object getValue(O pojo, Field<O,?> field) throws OrmException {
        if (field == null) {
            throw new OrmException("Null field type passed to getValue(). BUG!");
        }
        if (pojo.getClass().isRecord()) {
            return getComponentValue(pojo, field);
        }
        java.lang.reflect.Field refField = getDeclaredField(pojo.getClass(), field.getJavaName());
        return switch (field.getFieldType()) {
            case LONG -> getLong(pojo, refField);
            case INTEGER -> getInteger(pojo, refField);
            case SHORT -> getShort(pojo, refField);
            case BYTE -> getByte(pojo, refField);
            case DOUBLE -> getDouble(pojo, refField);
            case FLOAT -> getFloat(pojo, refField);
            case BOOLEAN -> getBoolean(pojo, refField);
            case ENUM -> getEnum(pojo, refField);
            case DATE, INSTANT, LOCAL_DATE_TIME, STRING, BYTE_ARRAY -> getObject(pojo, refField);
        };
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
    public final <O> int compareTo(O pojo1, O pojo2, Field<O,?> field) throws OrmException {
        switch (field.getFieldType()) {
            case LONG, INTEGER, SHORT, BYTE, DOUBLE, FLOAT, BOOLEAN,
                    ENUM, STRING, DATE, LOCAL_DATE_TIME, INSTANT -> {
                Object val1 = getValue(pojo1, field);
                Object val2 = getValue(pojo2, field);
                if (val1 == val2) return 0;
                if (val1 == null) return -1;
                if (val2 == null) return 1;
                if (val1 instanceof Comparable) {
                    //noinspection unchecked,rawtypes
                    return ((Comparable) val1).compareTo(val2);
                } else {
                    throw new OrmException(format("Non-comparable type %s for field %s", field.getJavaType(), field.getJavaName()));
                }
            }
            default -> throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }

    }

    protected abstract <O> O newPojoInstance(Class<O> type) throws OrmException;

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

    private static <O> Object getComponentValue(O obj, Field<O, ?> field) {
        var opt = Arrays.stream(obj.getClass().getRecordComponents())
                .filter(com -> com.getName().equals(field.getJavaName()))
                .findFirst();
        if (opt.isEmpty()) {
            throw new UncaughtOrmException(format("Cannot find record component for field '%s' on %s", field.getJavaName(), obj.getClass().getSimpleName()));
        }
        var meth = opt.get().getAccessor();
        try {
            return meth.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UncaughtOrmException(e.getMessage(), e);
        }
    }
}

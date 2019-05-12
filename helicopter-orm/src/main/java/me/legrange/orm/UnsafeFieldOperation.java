package me.legrange.orm;

import static java.lang.String.format;
import sun.misc.Unsafe;

/**
 *
 * @author gideon
 */
class UnsafeFieldOperation implements PojoOperations {

    private final Unsafe unsafe;

    @Override
    public Object newPojoInstance(Table table) throws OrmException {
        try {
            return unsafe.allocateInstance(table.getObjectClass());
        } catch (InstantiationException ex) {
            throw new OrmException(ex.getMessage(), ex);
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
            case STRING:
                return getObject(pojo, refField);
            default:
                throw new OrmException(format("Unsupported field type '%s'. BUG!", field.getFieldType()));
        }
    }

    UnsafeFieldOperation() throws OrmException {
        unsafe = getUnsafe();
    }

    private Object getBoolean(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getBoolean(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    private Object getLong(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getLong(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    private Object getInteger(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getInt(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    private Object getShort(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getShort(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    private Object getByte(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getByte(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    private Object getDouble(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getDouble(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    private Object getFloat(Object pojo, java.lang.reflect.Field field) {
        if (field.getType().isPrimitive()) {
            return unsafe.getFloat(pojo, unsafe.objectFieldOffset(field));
        }
        return getObject(pojo, field);
    }

    private Object getEnum(Object pojo, java.lang.reflect.Field field) throws OrmException {
        Object value = getObject(pojo, field);
        Class<?> valueClass = value.getClass();
        if (valueClass.isEnum()) {
            return ((Enum) value).name();
        }
        throw new OrmException(format("Cannot read Pojo enum field from data of type %s", valueClass.getSimpleName()));
    }

    private Object getObject(Object pojo, java.lang.reflect.Field field) {
        return unsafe.getObject(pojo, unsafe.objectFieldOffset(field));
    }

    private void setBoolean(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putBoolean(pojo, unsafe.objectFieldOffset(field), (boolean) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    private void setLong(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putLong(pojo, unsafe.objectFieldOffset(field), (long) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    private void setInteger(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putInt(pojo, unsafe.objectFieldOffset(field), (int) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    private void setShort(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putShort(pojo, unsafe.objectFieldOffset(field), (short) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    private void setByte(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putByte(pojo, unsafe.objectFieldOffset(field), (byte) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    private void setDouble(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putDouble(pojo, unsafe.objectFieldOffset(field), (double) value);
        } else {
            setObject(pojo, field, value);
        }
    }

    private void setFloat(Object pojo, java.lang.reflect.Field field, Object value) {
        if (field.getType().isPrimitive()) {
            unsafe.putFloat(pojo, unsafe.objectFieldOffset(field), (float) value);
        } else {
            setObject(pojo, field, value);
        }
    }

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

    private void setObject(Object pojo, java.lang.reflect.Field field, Object value) {
        unsafe.putObject(pojo, unsafe.objectFieldOffset(field), value);
    }

    private Unsafe getUnsafe() throws OrmException {
        try {
            java.lang.reflect.Field f = Unsafe.class
                    .getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new OrmException(format("Error getting Unsafe (%s)", ex.getMessage()), ex);
        }
    }

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

}

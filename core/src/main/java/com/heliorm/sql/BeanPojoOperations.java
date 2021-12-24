package com.heliorm.sql;

import com.heliorm.OrmException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;

/**
 * An implementation of PojoOperations that uses the general JavaBean convention to
 * manipulate POJOs.
 *
 * Default public constructors and public getters and public setters are required.
 *
 * @author gideon
 */
public final class BeanPojoOperations extends AbstractPojoOperations {

    public BeanPojoOperations() throws OrmException {
    }

    @Override
    public Object newPojoInstance(Class<?> type) throws OrmException {
        try {
            return type.getConstructor(new Class[]{}).newInstance();
        } catch (NoSuchMethodException e) {
            throw new OrmException(format("No default constructor found for %s", type.getCanonicalName()), e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new OrmException(format("Error calling default constructor for %s (%s)", type.getCanonicalName(), e.getMessage()), e);
        }
    }

    @Override
    protected Object getByte(Object pojo, java.lang.reflect.Field field) throws OrmException {
        if (field.getType().isPrimitive()) {
            return getObject(pojo, field, Byte.TYPE);
        }
        return getObject(pojo, field, Byte.class);
    }

    @Override
    protected Object getShort(Object pojo, java.lang.reflect.Field field) throws OrmException {
        if (field.getType().isPrimitive()) {
            return getObject(pojo, field, Short.TYPE);
        }
        return getObject(pojo, field, Short.class);
    }

    @Override
    protected Object getInteger(Object pojo, java.lang.reflect.Field field) throws OrmException {
        if (field.getType().isPrimitive()) {
            return getObject(pojo, field, Integer.TYPE);
        }
        return getObject(pojo, field, Integer.class);
    }

    @Override
    protected Object getLong(Object pojo, java.lang.reflect.Field field) throws OrmException {
        if (field.getType().isPrimitive()) {
            return getObject(pojo, field, Long.TYPE);
        }
        return getObject(pojo, field, Long.class);
    }

    @Override
    protected Object getDouble(Object pojo, java.lang.reflect.Field field) throws OrmException {
        if (field.getType().isPrimitive()) {
            return getObject(pojo, field, Double.TYPE);
        }
        return getObject(pojo, field, Double.class);
    }

    @Override
    protected Object getFloat(Object pojo, java.lang.reflect.Field field) throws OrmException {
        if (field.getType().isPrimitive()) {
            return getObject(pojo, field, Float.TYPE);
        }
        return getObject(pojo, field, Float.class);
    }

    @Override
    protected Object getBoolean(Object pojo, java.lang.reflect.Field field) throws OrmException {
        if (field.getType().isPrimitive()) {
            return getObject(pojo, field, Boolean.TYPE);
        }
        return getObject(pojo, field, Boolean.class);
    }

    @Override
    protected Object getEnum(Object pojo, java.lang.reflect.Field field) throws OrmException {
        return getObject(pojo, field);
    }

    @Override
    protected Object getObject(Object pojo, java.lang.reflect.Field field) throws OrmException {
        return getObject(pojo, field, field.getType());
    }

    private Object getObject(Object pojo, java.lang.reflect.Field field, Class<?> type) throws OrmException {
        String name = toCamel("get", field.getName());
        Optional<Method> method = findMethod(pojo.getClass(), name, new Class[]{});
        if (!method.isPresent()) {
            if (Boolean.class.isAssignableFrom(type) || Boolean.TYPE.isAssignableFrom(type)) {
                 name = toCamel("is", field.getName());
                 method = findMethod(pojo.getClass(), name, new Class[]{});
            }
        }
        try {
            if (method.isPresent()) {
                return method.get().invoke(pojo);
            } else {
                throw new OrmException(format("Could not find get method '%s' for field '%s' on %s", name, field.getName(), pojo.getClass().getCanonicalName()));
            }
        } catch (IllegalAccessException | InvocationTargetException | OrmException ex) {
            throw new OrmException(format("Error calling get method '%s' for field '%s' on %s", name, field.getName(), pojo.getClass().getCanonicalName()), ex);
        }
    }

    @Override
    protected void setByte(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (field.getType().isPrimitive()) {
            setObject(pojo, field, Byte.TYPE, value);
        } else {
            setObject(pojo, field, Byte.class, value);
        }
    }

    @Override
    protected void setShort(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (field.getType().isPrimitive()) {
            setObject(pojo, field, Short.TYPE, value);
        } else {
            setObject(pojo, field, Short.class, value);
        }
    }

    @Override
    protected void setInteger(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (field.getType().isPrimitive()) {
            setObject(pojo, field, Integer.TYPE, value);
        } else {
            setObject(pojo, field, Integer.class, value);
        }
    }

    @Override
    protected void setLong(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (field.getType().isPrimitive()) {
            setObject(pojo, field, Long.TYPE, value);
        } else {
            setObject(pojo, field, Long.class, value);
        }
    }

    @Override
    protected void setDouble(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (field.getType().isPrimitive()) {
            setObject(pojo, field, Double.TYPE, value);
        } else {
            setObject(pojo, field, Double.class, value);
        }
    }

    @Override
    protected void setFloat(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (field.getType().isPrimitive()) {
            setObject(pojo, field, Float.TYPE, value);
        } else {
            setObject(pojo, field, Float.class, value);
        }
    }

    @Override
    protected void setBoolean(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        if (field.getType().isPrimitive()) {
            setObject(pojo, field, Boolean.TYPE, value);
        } else {
            setObject(pojo, field, Boolean.class, value);
        }
    }

    @Override
    protected void setEnum(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        Class<?> valueClass = value.getClass();
        if (valueClass.isEnum()) {
            setObject(pojo, field, value);
        } else {
            if (!String.class.isAssignableFrom(valueClass)) {
                throw new OrmException(format("Cannot update Pojo enum field from data of type %s", valueClass.getSimpleName()));
            }
            Class<?> fieldClass = field.getType();
            if (!fieldClass.isEnum()) {
                throw new OrmException(format("Field '%s' on %s is supposed to be an emum type. BUG!", field.getName(), field.getDeclaringClass().getSimpleName()));
            }
            setObject(pojo, field, Enum.valueOf((Class<Enum>) fieldClass, (String) value));
        }

    }

    @Override
    protected void setObject(Object pojo, java.lang.reflect.Field field, Object value) throws OrmException {
        setObject(pojo, field, value.getClass(), value);
    }

    private void setObject(Object pojo, java.lang.reflect.Field field, Class<?> type, Object value) throws OrmException {
        String name = toCamel("set", field.getName());
        Optional<Method> method = findMethod(pojo.getClass(), name, new Class[]{type});
        try {
            if (method.isPresent()) {
                method.get().invoke(pojo, value);
            } else {
                throw new OrmException(format("Could not find set method '%s' for field '%s' on %s", name, field.getName(), pojo.getClass().getCanonicalName()));
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new OrmException(format("Error calling set method '%s' for field '%s' on %s", name, field.getName(), pojo.getClass().getCanonicalName()), ex);
        }

    }

    /**
     * Find a method with the given name and parameters on the given class.
     *
     * @param type   The class on which to find the method
     * @param name   The name of the method
     * @param params The parameter types
     * @return The method if found or empty if not.
     */
    private Optional<Method> findMethod(Class<?> type, String name, Class[] params) {
        try {
            return Optional.of(type.getMethod(name, params));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    /**
     * Create camel case from an array of strings
     *
     * @param args The strings
     * @return The camel case string
     */
    private String toCamel(String... args) {
        return Arrays.stream(args).reduce((l,r) -> l + Character.toUpperCase(r.charAt(0)) + r.substring(1)).get();
    }
}

package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Field;

import static java.lang.String.format;

/**
 *
 * @author gideon
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 */
public abstract class FieldPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements Field<T, O, C>, Cloneable {

    private final Class<C> fieldClass;
    private final String javaName;
    private final String sqlName;
    private final boolean primaryKey;

    public FieldPart(Class<C> fieldClass, String javaName, String sqlName) {
        this(fieldClass, javaName, sqlName, false);
    }

    public FieldPart(Class<C> fieldClass, String javaName, String sqlName, boolean primaryKey) {
        super(Type.FIELD, null);
        this.fieldClass = fieldClass;
        this.javaName = javaName;
        this.sqlName = sqlName;
        this.primaryKey = primaryKey;
    }

    @Override
    public Class<C> getJavaType() {
        return fieldClass;
    }

    @Override
    public String getJavaName() {
        return javaName;
    }

    @Override
    public String getSqlName() {
        return sqlName;
    }

    @Override
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public boolean isAutoNumber() {
        return false;
    }

    public final FieldPart<T, O, C> getThis() throws OrmException {
        try {
            return (FieldPart<T, O, C>) clone();
        } catch (CloneNotSupportedException ex) {
            throw new OrmException(format("Could not make a copy of class %s.BUG!", getClass().getSimpleName()));
        }
    }

    @Override
    public String toString() {
        return format("%s", getJavaName());
    }

}

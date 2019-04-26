package me.legrange.orm.impl;

import me.legrange.orm.Field;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 * @param <C>
 */
public abstract class FieldPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements Field<T, O, C> {

    private final Class<C> fieldClass;
    private final String javaName;
    private final String sqlName;

    public FieldPart(Class<C> fieldClass, String javaName, String sqlName) {
        super(null);
        this.fieldClass = fieldClass;
        this.javaName = javaName;
        this.sqlName = sqlName;
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
    public Type getType() {
        return Type.FIELD;
    }

    public final FieldPart<T, O, C> getThis() {
        return this;
    }
}

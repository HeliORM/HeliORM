package net.legrange.orm.impl;

import static java.lang.String.format;
import net.legrange.orm.Field;
import net.legrange.orm.OrmException;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 * @param <C>
 */
public abstract class FieldPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements Field<T, O, C>, Cloneable {

    private final Class<C> fieldClass;
    private final String javaName;
    private final String sqlName;
    private final boolean primaryKey;
    private Part left;

    public FieldPart(Class<C> fieldClass, String javaName, String sqlName) {
        this(fieldClass, javaName, sqlName, false);
    }

    public FieldPart(Class<C> fieldClass, String javaName, String sqlName, boolean primaryKey) {
        super(null);
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
    public Type getType() {
        return Type.FIELD;
    }

    @Override
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public final FieldPart<T, O, C> getThis() throws OrmException {
        try {
            return (FieldPart<T, O, C>) clone();
        } catch (CloneNotSupportedException ex) {
            throw new OrmException(format("Could not make a copy of class %s.BUG!", getClass().getSimpleName()));
        }
    }

    @Override
    public Part left() {
        if (left == null) {
            return super.left();
        }
        return left;
    }

    void setLeft(Part left) {
        this.left = left;
    }

    @Override
    public String toString() {
        return format("%s", getJavaName());
    }

}

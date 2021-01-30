package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Field;

import java.util.Optional;

import static com.heliorm.impl.Part.Type.FIELD;
import static java.lang.String.format;

/**
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public abstract class FieldPart<T extends Table<O>, O, C> extends Part<T, O, T, O> implements Field<T, O, C>, Cloneable {

    private final FieldType fieldType;
    private final Class<C> javaType;
    private final String javaName;
    private String sqlName;
    private boolean primaryKey = false;
    private boolean autoNumber = false;
    private boolean foreignKey = false;
    private boolean nullable = false;
    private Optional<Table<?>> foreignTable = Optional.empty();
    private final Optional<Integer> length = Optional.empty();

    public FieldPart(FieldType fieldType, Class<C> javaType, String javaName) {
        super(Part.Type.FIELD, null);
        this.fieldType = fieldType;
        this.javaType = javaType;
        this.javaName = javaName;
        this.sqlName = javaName;
    }

    @Override
    public final Class<C> getJavaType() {
        return javaType;
    }

    @Override
    public final String getJavaName() {
        return javaName;
    }

    @Override
    public final String getSqlName() {
        return sqlName;
    }

    @Override
    public final boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public boolean isAutoNumber() {
        return autoNumber;
    }

    public final FieldPart<T, O, C> getThis() throws OrmException {
        try {
            return (FieldPart<T, O, C>) clone();
        } catch (CloneNotSupportedException ex) {
            throw new OrmException(format("Could not make a copy of class %s.BUG!", getClass().getSimpleName()));
        }
    }

    @Override
    public final FieldType getFieldType() {
        return fieldType;
    }

    @Override
    public final boolean isForeignKey() {
        return foreignKey;
    }

    @Override
    public final Optional<Table<?>> getForeignTable() {
        return foreignTable;
    }

    @Override
    public final Optional<Integer> getLength() {
        return length;
    }

    @Override
    public final boolean isNullable() {
        return nullable;
    }

    void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    void setAutoNumber(boolean autoNumber) {
        this.autoNumber = autoNumber;
    }

    void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    void setForeignTable(Optional<Table<?>> foreignTable) {
        this.foreignTable = foreignTable;
    }

    @Override
    public String toString() {
        return format("%s", getJavaName());
    }

}

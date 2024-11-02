package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.FieldOrder;

import java.util.Optional;

import static java.lang.String.format;

/**
 * @param <O> Type of POJO
 * @param <C> Type of the field
 * @author gideon
 */
public abstract class FieldPart<O, C> implements Field<O, C>, Cloneable {

    private final Table<O> table;
    private final FieldType fieldType;
    private final Class<C> javaType;
    private final String javaName;
    private String sqlName;
    private boolean primaryKey = false;
    private boolean autoNumber = false;
    private boolean foreignKey = false;
    private boolean nullable = false;
    private Table<?> foreignTable = null;
    private Integer length = null;


    public FieldPart(Table<O> table, FieldType fieldType, Class<C> javaType, String javaName) {
        this.table = table;
        this.fieldType = fieldType;
        this.javaType = javaType;
        this.javaName = javaName;
        this.sqlName = javaName;
    }

    @Override
    public final Table<O> getTable() {
        return table;
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

    void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    @Override
    public final boolean isPrimaryKey() {
        return primaryKey;
    }

    void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public boolean isAutoNumber() {
        return autoNumber;
    }

    void setAutoNumber(boolean autoNumber) {
        this.autoNumber = autoNumber;
    }

    public final FieldPart<O, C> getThis() throws OrmException {
        try {
            //noinspection unchecked
            return (FieldPart<O, C>) clone();
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

    void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    @Override
    public final Optional<Table<?>> getForeignTable() {
        return Optional.ofNullable(foreignTable);
    }

    void setForeignTable(Table<?> foreignTable) {
        this.foreignTable = foreignTable;
    }

    @Override
    public final Optional<Integer> getLength() {
        return Optional.ofNullable(length);
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public final boolean isNullable() {
        return nullable;
    }

    void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public FieldOrder<O, C> asc() {
        return () -> FieldPart.this;
    }

    @Override
    public FieldOrder<O, C> desc() {
        return new FieldOrder<>() {

            @Override
            public Direction getDirection() {
                return Direction.DESC;
            }

            @Override
            public Field<O, C> getField() {
                return FieldPart.this;
            }
        };
    }

    @Override
    public Field<O, C> getField() {
        return this;
    }

    @Override
    public String toString() {
        return format("%s : %s", getJavaName(), getFieldType());
    }

}

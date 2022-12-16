package com.heliorm.impl;

import com.heliorm.def.FieldOrder;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.Field;

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
    private Optional<String> foreignTable = Optional.empty();
    private Optional<Integer> length = Optional.empty();
    private boolean collection = false;
    private Optional<String> collectionTable = Optional.empty();

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

    @Override
    public final boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public boolean isAutoNumber() {
        return autoNumber;
    }

    public final FieldPart<O, C> getThis() throws OrmException {
        try {
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

    @Override
    public final Optional<Table<?>> getForeignTable() {
        if (foreignTable.isPresent()) {
            return getTable().getDatabase().getTables().stream()
                    .filter(table -> table.getSqlTable().equals(foreignTable.get()))
                    .findFirst();
        }
        return Optional.empty();
    }

    @Override
    public final Optional<Integer> getLength() {
        return length;
    }

    @Override
    public final boolean isNullable() {
        return nullable;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    @Override
    public Optional<Table<?>> getCollectionTable() {
        if (collectionTable.isPresent()) {
            return getTable().getDatabase().getTables().stream()
                    .filter(table -> table.getSqlTable().equals(collectionTable.get()))
                    .findFirst();
        }
        return Optional.empty();    }
    public FieldOrder<O,C> asc() {
        return () -> FieldPart.this;
    }

    public FieldOrder<O,C> desc() {
        return new FieldOrder<O, C>() {

                @Override
                public Direction getDirection () {
                return Direction.DESC;
            }

                @Override
                public Field<O, C> getField () {
                return FieldPart.this;
            }

        };
    }


    @Override
    public Field<O, C> getField() {
        return this;
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

    void setForeignTable(Optional<String> name) {
        this.foreignTable = name;
    }

    void setCollection(boolean collection) {
        this.collection = collection;
    }

    void setCollectionTableName(Optional<String> name) {
        this.collectionTable = name;
    }

    public void setLength(Optional<Integer> length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return format("%s : %s", getJavaName(), getFieldType());
    }

}

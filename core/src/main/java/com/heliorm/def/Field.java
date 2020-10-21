package com.heliorm.def;

import com.heliorm.Table;

import java.util.Optional;

/**
 * A field on a table.
 *
 * @param <T> The type of the table
 * @param <O> The type of the POJO to which the table and this field applies
 * @param <C> The data type of the field
 * @author gideon
 */
public interface Field<T extends Table<O>, O, C> {

    enum FieldType {
        LONG,
        INTEGER,
        SHORT,
        BYTE,
        DOUBLE,
        FLOAT,
        BOOLEAN,
        ENUM,
        STRING,
        DATE,
        TIMESTAMP,
        DURATION;
    }

    /**
     * Get the type of the field.
     *
     * @return The field type
     */
    FieldType getFieldType();

    /**
     * Get the java type fo the field
     *
     * @return The java type
     */
    Class<C> getJavaType();

    /**
     * Get the java field name of the field.
     *
     * @return The field name
     */
    String getJavaName();

    /**
     * Get the SQL column name of the field.
     *
     * @return The SQL column name
     */
    String getSqlName();

    /**
     * Returns true if this field represents the table's primary key.
     *
     * @return True if it is
     */
    boolean isPrimaryKey();

    /** Return true if this field is the foreign key from another table.
     *
     * @return True if it is
     */
    default boolean isForeignKey() {
        return false;
    }

    /**
     * Return true if this is an auto-number key
     *
     * @return True if auto-number
     */
    boolean isAutoNumber();

    /**
     * Return the table to which this field links if it is a foreign key.
     * @return The linked table
     */
    default Optional<Table<?>> getForeignTable() {
        return Optional.empty();
    }

    /**
     * Return the field length, if a specific length is known.
     *
     * @return The length, if known
     */
    default Optional<Integer> getLength() {
        return Optional.empty();
    }

    /** Return true if the field can be null.
     *
     * @return True if it can be null
     */
    default boolean isNullable() {
        return false;
    }
}

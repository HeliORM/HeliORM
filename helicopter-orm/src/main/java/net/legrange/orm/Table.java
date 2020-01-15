package net.legrange.orm;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.legrange.orm.def.Field;

/**
 * The interface that must be implemented to define a database table for use by
 * the ORM
 *
 * @author gideon
 * @param <O> The type of Object stored in the table.
 */
public interface Table<O> {

    /**
     * Return the type of the POJOs supported by this table.
     *
     * @return The type
     */
    Class<O> getObjectClass();

    /**
     * Return the persisted fields defined on POJOs for this table.
     *
     * @return The fields
     */
    List<Field> getFields();

    /**
     * Return the primary key field (if defined) for this table.
     *
     * @return The primary key field
     */
    Optional<Field> getPrimaryKey();

    /**
     * Return the SQL table name for this table.
     *
     * @return The table name
     */
    String getSqlTable();

    /**
     * Return tables which support POJOs which are concrete sub-classes of the POJO
     * supported by this table.
     *
     * @return The sub tables
     */
    Set<Table<?>> getSubTables();

    /**
     * Return the database within which this table exists.
     *
     * @return The database
     */
    Database getDatabase();

    /** Return true if this table refers to an abstract POJO and not to a concrete
     * POJO with a database table.
     *
     * @return true if abstract
     */
    boolean isAbstract();
}

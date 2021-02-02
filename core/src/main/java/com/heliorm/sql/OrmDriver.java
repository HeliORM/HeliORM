package com.heliorm.sql;

import java.util.stream.Stream;

import com.heliorm.impl.Part;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Executable;

/**
 * The interface that must be implemented to create a new (typically database)
 * driver for use with the ORM.
 *
 * @author gideon
 */
public interface OrmDriver {

    /**
     * Persist a new POJO to the database.
     *
     * @param <T> Type of the table for the POJO
     * @param <O> Type of the POJO
     * @param table The table to which the data is persisted
     * @param pojo The POJO
     * @return The updated POJO
     * @throws OrmException
     */
    <T extends Table<O>, O> O create(T table, O pojo) throws OrmException;

    /**
     * Persist an existing POJO to the database.
     *
     * @param <T> Type of the table for the POJO
     * @param <O> Type of the POJO
     * @param table The table to which the data is persisted
     * @param pojo The POJO
     * @return The updated POJO
     * @throws OrmException
     */
    <T extends Table<O>, O> O update(T table, O pojo) throws OrmException;

    /**
     * Delete a POJO from the database.
     *
     * @param <T> Type of the table for the POJO
     * @param <O> Type of the POJO
     * @param table The table from which the data is deleted
     * @param pojo The POJO
     * @throws OrmException
     */
    <T extends Table<O>, O> void delete(T table, O pojo) throws OrmException;

    /**
     * Query data from the database and stream the results.
     *
     * @param <O> The type of the POJO being loaded
     * @param <P> The type of the query part generated by the query builder
     * pattern
     * @param tail The tail of the query builder pattern
     * @return The stream of POJO results
     * @throws OrmException
     */
    <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException;
}
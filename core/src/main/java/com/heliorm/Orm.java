package com.heliorm;

import com.heliorm.def.Join;
import com.heliorm.def.Select;
import com.heliorm.def.Where;
import com.heliorm.impl.Selector;

public interface Orm extends AutoCloseable {

    /**
     * Persist a new POJO to the database.
     *
     * @param <O>  The type of the POJO
     * @param pojo The POJO to persist
     * @return The updated POJO
     * @throws OrmException Thrown if there is an error creating the POJO
     */
    <O> O create(O pojo) throws OrmException;

    /**
     * Persist an existing POJO to the database.
     *
     * @param <O>  The type of the POJO
     * @param pojo The POJO to persist
     * @return The updated POJO
     * @throws OrmException Thrown if there is an error updating the POJO
     */
    <O> O update(O pojo) throws OrmException;

    /**
     * Delete a POJO from the database.
     *
     * @param <O>  The type of the POJO
     * @param pojo The POJO to delete
     * @throws OrmException Thrown if there is an error deleting the POJO
     */
    <O> void delete(O pojo) throws OrmException;

    /**
     * Create a query builder pattern to select data from the database.
     *
     * @param <O>   The type of the POJOs selected
     * @param table The table to use as starting point for building the query.
     * @return The Select object that can be used to expand the query or to
     * query data.
     */
    <O> Select<O> select(Table<O> table);

    <O> Select<O> select(Table<O> table, Where<O> where);

    <O> Select<O> select(Table<O> table, Join<O>... joins);

    <O> Select<O> select(Table<O> table, Where<O> where, Join<O>... joins);

    /**
     * Open a new transaction.
     *
     * @return The transaction
     * @throws OrmException Thrown if the driver doesn't support transactions of if there is a problem opening one
     */
    OrmTransaction openTransaction() throws OrmException;

    @Override
    void close();

    /**
     * Find the table definition for the given POJO.
     *
     * @param <O>  The type of the POJO
     * @param pojo The pojo
     * @return The table
     * @throws OrmException Thrown if there is an error finding the table the POJO
     */
    <O> Table<O> tableFor(O pojo) throws OrmException;

    /**
     * Find the table definition for the given POJO.
     *
     * @param <O>  The type of the POJO
     * @param type The type of pojo
     * @return The table
     * @throws OrmException Thrown if there is an error finding the table the POJO
     */
    <O> Table<O> tableFor(Class<O> type) throws OrmException;

    /**
     * Return the selector that is used to select data.
     *
     * @return The selector
     */
    Selector selector();

}

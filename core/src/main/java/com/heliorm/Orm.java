package com.heliorm;

import com.heliorm.def.Select;
import com.heliorm.def.Executable;
import com.heliorm.impl.Part;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
     * @param <T>   The type of the table to select on
     * @param <O>   The type of the POJOs selected
     * @param table The table to use as starting point for building the query.
     * @return The Select object that can be used to expand the query or to
     * query data.
     */
    <T extends Table<O>, O> Select<T, O, T, O> select(T table);

    /** Open a new transaction.
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
     * @throws OrmException Thrown if there is an finding the table the POJO
     */
    <O> Table<O> tableFor(O pojo) throws OrmException;

    /**
     * Find the table definition for the given POJO.
     *
     * @param <O>  The type of the POJO
     * @param type The type of pojo
     * @return The table
     * @throws OrmException Thrown if there is an finding the table the POJO
     */
    <O> Table<O> tableFor(Class<O> type) throws OrmException;

    /**
     * Execute the supplied programmed query and return a list of result pojos.
     *
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The list of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <O, P extends Part & Executable> List<O> list(P tail) throws OrmException;

    /**
     * Execute the supplied programmed query and return a stream of result
     * pojos.
     *
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The stream of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException;

    /**
     * Execute the supplied programmed query and return an optional with a
     * possible result. It is expected that either zero or one results will be
     * found, so more than one result will cause an error.
     *
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The optional found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <O, P extends Part & Executable> Optional<O> optional(P tail) throws OrmException;

    /**
     * Execute the supplied programmed query and return exactly one matching
     * result. It is expected that exactly one result will be found, so no
     * result or more than one result will cause an error.
     *
     * @param <O>  The type of Pojo to return
     * @param <P>  The type of programmed query
     * @param tail The tail of the query
     * @return The found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <O, P extends Part & Executable> O one(P tail) throws OrmException;
}

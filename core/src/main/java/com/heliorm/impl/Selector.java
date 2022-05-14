package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Select;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This interface defines what an ORM implementation should provide to allow for the querying of data using
 * a structured query. This allows the implementation to deal with stream, list and optional in a way that
 * makes sense within the context of a specific implementation.
 *
 * @author gideon
 */
public interface Selector {

    /**
     * Execute the supplied programmed query and return a list of result pojos.
     *
     * @param <O>  The type of Pojo to return
     * @param tail The tail of the query
     * @return The list of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <T extends Table<O>, O> List<O> list(Select<T, O> tail) throws OrmException;

    /**
     * Execute the supplied programmed query and return a stream of result
     * pojos.
     *
     * @param <O>  The type of POJO to return
     * @param <T>  The rtpe of the table queried
     * @param tail The tail of the query
     * @return The stream of loaded Pojos
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <T extends Table<O>, O> Stream<O> stream(Select<T, O> tail) throws OrmException;

    /**
     * Execute the supplied programmed query and return an optional with a
     * possible result. It is expected that either zero or one results will be
     * found, so more than one result will cause an error.
     *
     * @param <O>  The type of POJO to return
     * @param <T>  The type of the table queried
     * @param tail The tail of the query
     * @return The optional found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <T extends Table<O>, O> Optional<O> optional(Select<T, O> tail) throws OrmException;

    /**
     * Execute the supplied programmed query and return exactly one matching
     * result. It is expected that exactly one result will be found, so no
     * result or more than one result will cause an error.
     *
     * @param <O>  The type of POJO to return
     * @param <T>  The type of the table queried
     * @param tail The tail of the query
     * @return The found Pojo
     * @throws OrmException Thrown if any of the large number of things that can
     *                      go wrong did go wrong.
     */
    <T extends Table<O>, O> O one(Select<T, O> tail) throws OrmException;

}

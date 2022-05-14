package com.heliorm.def;

import com.heliorm.OrmException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @param <O> Object type
 * @author gideon
 */
public interface Executable<O> {

    /**
     * Query data and return a list of POJOs.
     *
     * @return The list
     * @throws OrmException
     */
    List<O> list() throws OrmException;

    /**
     * Query data and return a stream of POJOs.
     *
     * @return The stream
     * @throws OrmException
     */
    Stream<O> stream() throws OrmException;

    O one() throws OrmException;

    Optional<O> optional() throws OrmException;

}

package com.heliorm.def;

import com.heliorm.OrmException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author gideon
 * @param <O> Object type
 */
public interface Executable<O> {

    List<O> list() throws OrmException;

    Stream<O> stream() throws OrmException;

    O one() throws OrmException;

    Optional<O> optional() throws OrmException;

}

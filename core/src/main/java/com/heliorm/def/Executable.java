package com.heliorm.def;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T> Table type
 * @param <O> Object type
 */
public interface Executable<T extends Table<O>, O> {

    List<O> list() throws OrmException;

    Stream<O> stream() throws OrmException;

    O one() throws OrmException;

    Optional<O> oneOrNone() throws OrmException;

}

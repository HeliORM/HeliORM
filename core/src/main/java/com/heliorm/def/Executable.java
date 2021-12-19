package com.heliorm.def;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <DT> Table type
 * @param <DO> Object type
 */
public interface Executable<DT extends Table<DO>, DO> {

    List<DO> list() throws OrmException;

    Stream<DO> stream() throws OrmException;

    DO one() throws OrmException;

    Optional<DO> optional() throws OrmException;

}

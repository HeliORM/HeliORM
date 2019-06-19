package net.legrange.orm;

import java.util.stream.Stream;
import net.legrange.orm.def.Executable;
import net.legrange.orm.impl.Part;

/**
 *
 * @author gideon
 */
public interface OrmDriver {

    <T extends Table<O>, O> O create(T table, O pojo) throws OrmException;

    <T extends Table<O>, O> O update(T table, O pojo) throws OrmException;

    <T extends Table<O>, O> void delete(T table, O pojo) throws OrmException;

    <O, P extends Part & Executable> Stream<O> stream(P tail) throws OrmException;
}

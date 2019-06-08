package me.legrange.orm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author gideon
 * @param <O> The type of Object stored in the table.
 */
public interface Table<O> {

    Class<O> getObjectClass();

    List<Field> getFields();

    Optional<Field> getPrimaryKey();

    String getSqlTable();

    Set<Table> getSubTables();
}

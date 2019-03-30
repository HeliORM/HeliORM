package me.legrange.orm;

import java.util.List;

/**
 *
 * @author gideon
 * @param <O> The type of Object stored in the table.
 */
public interface Table<O> {

    Class<O> getObjectClass();

    List<Field> getFields();

    String getSqTable();

}

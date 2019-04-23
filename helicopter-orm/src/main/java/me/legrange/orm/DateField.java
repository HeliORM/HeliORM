package me.legrange.orm;

import java.util.Date;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DateField<T extends Table<O>, O> extends Field<T, O, Date> {

}

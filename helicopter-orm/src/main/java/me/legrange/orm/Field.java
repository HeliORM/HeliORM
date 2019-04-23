package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 * @param <C>
 */
public interface Field<T extends Table<O>, O, C> {

    Class<C> getJavaType();

    String getJavaName();

    String getSqlName();

}

package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Order<T extends Table<O>, O> extends Executable {

    <F extends Field<T, O, C>, C> Order<T, O> thenBy(F field);

    <F extends Field<T, O, C>, C> Order<T, O> thenByDesc(F field);

}

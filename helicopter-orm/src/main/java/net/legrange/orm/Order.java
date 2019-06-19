package net.legrange.orm;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Order<T extends Table<O>, O> {

    <F extends Field<T, O, C>, C> Ordered<T, O> orderBy(F field);

    <F extends Field<T, O, C>, C> Ordered<T, O> orderByDesc(F field);

}

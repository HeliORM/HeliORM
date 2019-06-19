package net.legrange.orm.def;

import net.legrange.orm.Table;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Ordered<T extends Table<O>, O> extends Executable<T, O> {

    <F extends Field<T, O, C>, C> Ordered<T, O> thenBy(F field);

    <F extends Field<T, O, C>, C> Ordered<T, O> thenByDesc(F field);

}

package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T> Type of the table
 * @param <O> Type of the POJO
 */
public interface Order<T extends Table<O>, O> {

    <F extends Field<T, O, C>, C> Ordered<T, O> orderBy(F field);

    <F extends Field<T, O, C>, C> Ordered<T, O> orderByDesc(F field);

}

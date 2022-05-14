package com.heliorm.def;

import com.heliorm.Table;

/**
 * @param <T> Type of the table
 * @param <O> Type of the POJO
 * @author gideon
 */
public interface Order<T extends Table<O>, O> {

    <F extends FieldOrder<T, O, ?>> Complete<O> orderBy(F field, F... orders);


}

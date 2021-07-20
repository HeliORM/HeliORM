package com.heliorm.def;

import com.heliorm.FieldOrder;
import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T> Type of the table
 * @param <O> Type of the POJO
 */
public interface Order<T extends Table<O>, O> {

    <F extends FieldOrder<T, O, ?>> Ordered<T, O> orderBy(F field, F...orders);


}

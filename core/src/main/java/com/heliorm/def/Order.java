package com.heliorm.def;

/**
 * @param <T> Type of the table
 * @param <O> Type of the POJO
 * @author gideon
 */
public interface Order<O> {

    <F extends FieldOrder<O, ?>> Complete<O> orderBy(F field, F... orders);


}

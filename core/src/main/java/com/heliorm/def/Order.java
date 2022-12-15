package com.heliorm.def;

/**
 * @param <O> Type of the POJO
 * @author gideon
 */
public interface Order<O> {

    <F extends FieldOrder<O, ?>> Complete<O> orderBy(F field, F... orders);


}

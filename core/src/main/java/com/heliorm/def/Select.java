package com.heliorm.def;

/**
 * @param <DO> Type of POJO
 * @author gideon
 */
public interface Select<DO> extends Executable<DO>, Order<DO>, Limit<DO> {

    <F extends FieldOrder<DO, ?>> Complete<DO> orderBy(F order, F... orders);

}

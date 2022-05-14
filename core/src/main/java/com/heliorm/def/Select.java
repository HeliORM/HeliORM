package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <DT> Type of table
 * @param <DO> Type of POJO
 */
public interface Select<DT extends Table<DO>, DO> extends Executable<DO>, Order<DT, DO>, Limit<DO> {

     <F extends FieldOrder<DT, DO, ?>> Complete<DO> orderBy(F order, F... orders);

}

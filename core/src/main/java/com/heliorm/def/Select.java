package com.heliorm.def;

import com.heliorm.FieldOrder;
import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <DT> Type of table
 * @param <DO> Type of POJO
 * @param <LT> The type of the right hand table
 * @param <LO> Type of the right hand POJO
 */
public interface Select<DT extends Table<DO>, DO> extends Executable<DO>, Order<DT, DO> {

     <F extends FieldOrder<DT, DO, ?>> Executable<DO> orderBy(F order, F... orders);

}

package com.heliorm.def;

import com.heliorm.Table;

/**
 * @param <DT> Type of table
 * @param <DO> Type of POJO
 * @author gideon
 */
public interface Select<DT extends Table<DO>, DO> extends Executable<DO>, Order<DT, DO>, Limit<DO> {

    <F extends FieldOrder<DT, DO, ?>> Complete<DO> orderBy(F order, F... orders);

}

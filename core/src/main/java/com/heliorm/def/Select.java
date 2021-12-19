package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <DT> Type of table
 * @param <DO> Type of POJO
 * @param <LT> The type of the right hand table
 * @param <LO> Type of the right hand POJO
 */
public interface Select<DT extends Table<DO>, DO, LT extends Table<LO>, LO> extends Executable<DT, DO>, Order<DT, DO> {

    <RT extends Table<RO>, RO> Join<DT, DO, RT, RO> join(RT table);

    Continuation<DT, DO, LT, LO> where(ExpressionContinuation<LT, LO> cont);

}

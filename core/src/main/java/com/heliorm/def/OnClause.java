package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public interface OnClause<DT extends Table<DO>, DO, LT extends Table<LO>, LO, RT extends Table<RO>, RO>  extends Executable<DT, DO> {


    <NT extends Table<NO>, NO> Join<DT, DO, LT, LO, NT, NO> join(NT table);

    OnContinuation<DT, DO, RT, RO> where(ExpressionContinuation<RT, RO> cont);

    <NT extends Table<NO>, NO> Join<DT,DO,RT,RO,NT,NO> thenJoin(NT table);

}

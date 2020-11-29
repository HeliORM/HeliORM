package com.heliorm.def;

import com.heliorm.Table;

/**
 ** @param <LT> Table type
 * @param <LO> Object type
 *
 * @author gideon
 * @param <RT>
 * @param <RO>
 */
public interface Continuation<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Executable<LT, LO>, Order<LT, LO> {

    Continuation<LT, LO, RT, RO> and(ExpressionContinuation<RT, RO> cont);

    Continuation<LT, LO, RT, RO> or(ExpressionContinuation<RT, RO> cont);

    <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table);

}

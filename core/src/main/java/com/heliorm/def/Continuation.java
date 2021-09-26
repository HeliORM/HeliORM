package com.heliorm.def;

import com.heliorm.Table;

/**
 * @param <LT> Table type
 * @param <LO> Object type
 * @param <RT> The following table's type
 * @param <RO> The following table's object type
 *
 * @author gideon
 */
public interface Continuation<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Executable<LT, LO>, Order<LT, LO> {

    Continuation<LT, LO, RT, RO> and(ExpressionContinuation<RT, RO> cont);

    Continuation<LT, LO, RT, RO> or(ExpressionContinuation<RT, RO> cont);

    <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table);

}

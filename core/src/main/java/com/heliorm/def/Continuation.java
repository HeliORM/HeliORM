package com.heliorm.def;

import com.heliorm.Table;

/**
 * @param <DT> Table type
 * @param <DO> Object type
 * @param <LT> The following table's type
 * @param <LO> The following table's object type
 *
 * @author gideon
 */
public interface Continuation<DT extends Table<DO>, DO, LT extends Table<LO>, LO> extends Executable<DT, DO>, Order<DT, DO> {

    Continuation<DT, DO, LT, LO> and(ExpressionContinuation<LT, LO> cont);

    Continuation<DT, DO, LT, LO> or(ExpressionContinuation<LT, LO> cont);

    <RT extends Table<RO>, RO> Join<DT, DO, RT, RO> join(RT table);

}

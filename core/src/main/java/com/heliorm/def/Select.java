package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 * @param <T> Type of table
 * @param <O> Type of POJO
 * @param <RT> The type of the right hand table
 * @param <RO> Type of the right hand POJO
 */
public interface Select<T extends Table<O>, O, RT extends Table<RO>, RO> extends Executable<T, O>, Order<T, O> {

    <RT extends Table<RO>, RO> Join<T, O, RT, RO> join(RT table);

    Continuation<T, O, RT, RO> where(ExpressionContinuation<RT, RO> cont);

}

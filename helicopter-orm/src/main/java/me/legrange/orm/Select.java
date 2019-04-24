package me.legrange.orm;

/**
 *
 * @author gideon
 * @param <T>
 * @param <O>
 */
public interface Select<T extends Table<O>, O, RT extends Table<RO>, RO> extends Executable<T, O>, Order<T, O> {

    <RT extends Table<RO>, RO> Join<T, O, RT, RO> join(RT table);

    Continuation<T, O, RT, RO> where(ExpressionContinuation<RT, RO> cont);

}

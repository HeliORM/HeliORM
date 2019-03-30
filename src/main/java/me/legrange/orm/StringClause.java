package me.legrange.orm;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface StringClause<T extends Table<O>, O, RT extends Table<RO>, RO> extends Clause<T, O, String, RT, RO>,
        WithRange<T, O, String, RT, RO>, WithEquals<T, O, String, RT, RO>, WithIn<T, O, String, RT, RO> {

}

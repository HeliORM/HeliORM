package me.legrange.orm;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface BooleanClause<T extends Table<O>, O, RT extends Table<RO>, RO> extends Clause<T, O, Boolean, RT, RO>,
        WithRange<T, O, Boolean, RT, RO>, WithEquals<T, O, Boolean, RT, RO>, WithIn<T, O, Boolean, RT, RO> {

}

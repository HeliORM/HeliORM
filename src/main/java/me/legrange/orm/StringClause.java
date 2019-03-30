package me.legrange.orm;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface StringClause<T extends Table<O>, O> extends Clause<T, O, String>,
        WithRange<T, O, String>, WithEquals<T, O, String>, WithIn<T, O, String> {

}

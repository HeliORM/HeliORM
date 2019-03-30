package me.legrange.orm;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 *
 * @author gideon
 */
public interface NumberClause<T extends Table<O>, O, C> extends Clause<T, O, C>,
        WithRange<T, O, C>, WithEquals<T, O, C>, WithIn<T, O, C> {

}

package me.legrange.orm;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 * @param <C> Column/field type
 *
 * @author gideon
 */
public interface EnumClause<T extends Table<O>, O, C, RT extends Table<RO>, RO> extends Clause<T, O, C, RT, RO>,
        WithEquals<T, O, C, RT, RO>, WithIn<T, O, C, RT, RO> {

}

package me.legrange.orm;

import java.util.Date;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DateClause<T extends Table<O>, O, RT extends Table<RO>, RO> extends Clause<T, O, Date, RT, RO>,
        WithRange<T, O, Date, RT, RO>, WithEquals<T, O, Date, RT, RO>, WithIn<T, O, Date, RT, RO> {

}

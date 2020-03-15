package net.legrange.orm.def;

import java.util.Date;
import net.legrange.orm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DateExpression<T extends Table<O>, O> extends Expression<T, O, Date>,
        WithRange<T, O, Date>,
        WithEquals<T, O, Date>,
        WithIn<T, O, Date> {

}

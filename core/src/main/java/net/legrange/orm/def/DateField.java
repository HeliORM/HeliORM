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
public interface DateField<T extends Table<O>, O> extends Field<T, O, Date>, DateExpression<T, O> {

    @Override
    public default FieldType getFieldType() {
        return FieldType.DATE;
    }
}

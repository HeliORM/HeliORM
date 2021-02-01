package com.heliorm.def;

import java.util.Date;
import com.heliorm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DateField<T extends Table<O>, O> extends Field<T, O, Date>, DateExpression<T, O> {

    @Override
   default FieldType getFieldType() {
        return FieldType.DATE;
    }
}

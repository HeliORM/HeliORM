package com.heliorm.def;

import java.time.Duration;
import com.heliorm.Table;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DurationField<T extends Table<O>, O> extends Field<T, O, Duration>, DurationExpression<T, O> {

    @Override
   default FieldType getFieldType() {
        return FieldType.DURATION;
    }
}

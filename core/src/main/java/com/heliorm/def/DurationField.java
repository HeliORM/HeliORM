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
public interface DurationField<T extends Table<O>, O> extends Field<T, O, Duration>, Expression<T, O, Duration>, WithRange<T, O, Duration>, WithEquals<T, O, Duration>, WithIn<T, O, Duration>, WithIs<T, O, Duration> {

    @Override
   default FieldType getFieldType() {
        return FieldType.DURATION;
    }
}

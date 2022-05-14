package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.Table;

import java.time.Duration;

/**
 * A field representing a Duration value
 *
 * @param <T> Table type
 * @param <O> Object type
 * @author gideon
 * @deprecated Duration is a good candidate to rather use a type adapter for
 */
@Deprecated()
public interface DurationField<T extends Table<O>, O> extends Field<T, O, Duration>, WithRange<T, O, Duration>, WithEquals<T, O, Duration>, WithIn<T, O, Duration>, WithIs<T, O, Duration> {

    @Override
    default FieldType getFieldType() {
        return FieldType.DURATION;
    }
}

package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.Table;

import java.time.Instant;

/**
 * A field representing a Instant value
 *
 * @param <T> Table type
 * @param <O> Object type
 * @author gideon
 * @deprecated Duration is a good candidate to rather use a type adapter for
 */
@Deprecated
public interface InstantField<T extends Table<O>, O> extends Field<T, O, Instant>, WithRange<T, O, Instant>, WithEquals<T, O, Instant>, WithIn<T, O, Instant>, WithIs<T, O, Instant> {

    @Override
    default FieldType getFieldType() {
        return FieldType.INSTANT;
    }
}

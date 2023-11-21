package com.heliorm.def;

import com.heliorm.Field;

import java.time.Instant;

/**
 * A field representing a Instant value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface InstantField<O> extends Field<O, Instant>, WithRange<O, Instant>, WithEquals<O, Instant>, WithIn<O, Instant>, WithIs<O> {

    @Override
    default FieldType getFieldType() {
        return FieldType.INSTANT;
    }
}

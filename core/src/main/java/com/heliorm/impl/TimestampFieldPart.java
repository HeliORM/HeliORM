package com.heliorm.impl;

import com.heliorm.def.TimestampField;
import com.heliorm.Table;

import java.time.Instant;

/**
 *
 * @author gideon
 */
public class TimestampFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Instant> implements
        TimestampField<T, O>,
        WithRangePart<T, O, Instant>,
        WithEqualsPart<T, O, Instant>,
        WithInPart<T, O, Instant>, WithIsPart<T,O,Instant> {

    public TimestampFieldPart(String javaName) {
        super(FieldType.TIMESTAMP, Instant.class, javaName);
    }

}

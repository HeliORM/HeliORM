package com.heliorm.impl;

import com.heliorm.def.DurationField;
import com.heliorm.Table;

import java.time.Duration;

/**
 *
 * @author gideon
 */
public class DurationFieldPart<T extends Table<O>, O> extends FieldPart<T, O, Duration> implements
        DurationField<T, O>,
        WithRangePart<T, O, Duration>,
        WithEqualsPart<T, O, Duration>,
        WithInPart<T, O, Duration>, WithIsPart<T, O, Duration> {

    public DurationFieldPart(T table, String javaName) {
        super(table, FieldType.DURATION, Duration.class, javaName);
    }

}

package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.DurationField;

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

    public DurationFieldPart(String javaName, String sqlName) {
        super(Duration.class, javaName, sqlName);
    }

}

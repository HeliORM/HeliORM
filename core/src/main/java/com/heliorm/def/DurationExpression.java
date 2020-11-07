package com.heliorm.def;

import com.heliorm.Table;

import java.time.Duration;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DurationExpression<T extends Table<O>, O> extends Expression<T, O, Duration>,
        WithRange<T, O, Duration>,
        WithEquals<T, O, Duration>,
        WithIn<T, O, Duration>,
        WithIs<T,O,Duration> {

}

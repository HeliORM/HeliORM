package com.heliorm.def;

import com.heliorm.Table;

import java.time.Instant;

/**
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface TimeExpresssion<T extends Table<O>, O> extends Expression<T, O, Instant>,
        WithRange<T, O, Instant>,
        WithEquals<T, O, Instant>,
        WithIn<T, O, Instant>,  WithIs<T,O,Instant> {

}

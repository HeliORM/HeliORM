package com.heliorm.def;

import com.heliorm.Field;

import java.time.LocalDate;


/**
 * A field representing a LocalDateTime value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface LocalDateField< O> extends Field< O, LocalDate>, WithRange<O, LocalDate>, WithEquals<O, LocalDate>, WithIn<O, LocalDate>, WithIs<O> {

}

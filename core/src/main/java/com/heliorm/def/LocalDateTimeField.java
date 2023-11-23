package com.heliorm.def;

import com.heliorm.Field;
import java.time.LocalDateTime;


/**
 * A field representing a LocalDateTime value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface LocalDateTimeField< O> extends Field< O, LocalDateTime>, WithRange<O, LocalDateTime>, WithEquals<O, LocalDateTime>, WithIn<O, LocalDateTime>, WithIs<O> {

}

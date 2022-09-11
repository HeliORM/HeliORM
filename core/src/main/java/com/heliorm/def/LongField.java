package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.OrmException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A field representing a long value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface LongField< O> extends Field<O, Long>, WithRange<O, Long>, WithEquals<O, Long>, WithIn<O, Long>, WithIs<O> {

    default Continuation<O> lt(Integer value) throws OrmException {
        return lt(value.longValue());
    }

    default Continuation<O> le(Integer value) throws OrmException {
        return le(value.longValue());
    }

    default Continuation<O> gt(Integer value) throws OrmException {
        return gt(value.longValue());
    }

    default Continuation<O> ge(Integer value) throws OrmException {
        return ge(value.longValue());
    }

    default Continuation<O> eq(Integer value) throws OrmException {
        return eq(value.longValue());
    }


    default Continuation<O> notEq(Integer value) throws OrmException {
        return notEq(value.longValue());
    }

    default Continuation<O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.longValue()).collect(Collectors.toList()));
    }

    default Continuation<O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.longValue()).collect(Collectors.toList()));
    }

}

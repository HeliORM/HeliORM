package com.heliorm.def;

import com.heliorm.OrmException;
import com.heliorm.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A field representing a long value
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface LongField<T extends Table<O>, O> extends Field<T, O, Long>, Expression<T, O, Long>, WithRange<T, O, Long>, WithEquals<T, O, Long>, WithIn<T, O, Long>, WithIs<T, O, Long> {

    default Continuation<T, O> lt(Integer value) throws OrmException {
        return lt(value.longValue());
    }

    default Continuation<T, O> le(Integer value) throws OrmException {
        return le(value.longValue());
    }

    default Continuation<T, O> gt(Integer value) throws OrmException {
        return gt(value.longValue());
    }

    default Continuation<T, O> ge(Integer value) throws OrmException {
        return ge(value.longValue());
    }

    default Continuation<T, O> eq(Integer value) throws OrmException {
        return eq(value.longValue());
    }


    default Continuation<T, O> notEq(Integer value) throws OrmException {
        return notEq(value.longValue());
    }

    default Continuation<T, O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.longValue()).collect(Collectors.toList()));
    }

    default Continuation<T, O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.longValue()).collect(Collectors.toList()));
    }

}

package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.OrmException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A field representing a double value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface DoubleField< O> extends Field<O, Double>, WithRange<O, Double>, WithEquals<O, Double>, WithIn<O, Double>, WithIs<O> {

    default Continuation<O> lt(Integer value) throws OrmException {
        return lt(value.doubleValue());
    }

    default Continuation<O> le(Integer value) throws OrmException {
        return le(value.doubleValue());
    }

    default Continuation<O> gt(Integer value) throws OrmException {
        return gt(value.doubleValue());
    }

    default Continuation<O> ge(Integer value) throws OrmException {
        return ge(value.doubleValue());
    }

    default Continuation<O> eq(Integer value) throws OrmException {
        return eq(value.doubleValue());
    }

    default Continuation<O> notEq(Integer value) throws OrmException {
        return notEq(value.doubleValue());
    }

    default Continuation<O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(Integer::doubleValue).collect(Collectors.toList()));
    }

    default Continuation<O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(Integer::doubleValue).collect(Collectors.toList()));
    }

    default Continuation<O> lt(Long value) throws OrmException {
        return lt(value.doubleValue());
    }

    default Continuation<O> le(Long value) throws OrmException {
        return le(value.doubleValue());
    }

    default Continuation<O> gt(Long value) throws OrmException {
        return gt(value.doubleValue());
    }

    default Continuation<O> ge(Long value) throws OrmException {
        return ge(value.doubleValue());
    }

    default Continuation<O> eq(Long value) throws OrmException {
        return eq(value.doubleValue());
    }

    default Continuation<O> notEq(Long value) throws OrmException {
        return notEq(value.doubleValue());
    }

    default Continuation<O> in(Long... values) throws OrmException {
        return in(Arrays.stream(values).map(Long::doubleValue).collect(Collectors.toList()));
    }

    default Continuation<O> notIn(Long... values) throws OrmException {
        return notIn(Arrays.stream(values).map(Long::doubleValue).collect(Collectors.toList()));
    }
}

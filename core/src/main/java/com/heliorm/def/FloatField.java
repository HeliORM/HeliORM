package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.OrmException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A field representing a float value
 *
 * @param <O> Object type
 * @author gideon
 */
public interface FloatField< O> extends Field<O, Float>, WithRange< O, Float>, WithEquals<O, Float>, WithIn<O, Float>, WithIs<O> {

    default Continuation<O> lt(Integer value) throws OrmException {
        return lt(value.floatValue());
    }

    default Continuation<O> le(Integer value) throws OrmException {
        return le(value.floatValue());
    }

    default Continuation<O> gt(Integer value) throws OrmException {
        return gt(value.floatValue());
    }

    default Continuation<O> ge(Integer value) throws OrmException {
        return ge(value.floatValue());
    }

    default Continuation<O> eq(Integer value) throws OrmException {
        return eq(value.floatValue());
    }

    default Continuation<O> notEq(Integer value) throws OrmException {
        return notEq(value.floatValue());
    }

    default Continuation<O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(Integer::floatValue).collect(Collectors.toList()));
    }

    default Continuation<O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(Integer::floatValue).collect(Collectors.toList()));
    }

    default Continuation<O> lt(Long value) throws OrmException {
        return lt(value.floatValue());
    }

    default Continuation<O> le(Long value) throws OrmException {
        return le(value.floatValue());
    }

    default Continuation<O> gt(Long value) throws OrmException {
        return gt(value.floatValue());
    }

    default Continuation<O> ge(Long value) throws OrmException {
        return ge(value.floatValue());
    }

    default Continuation<O> eq(Long value) throws OrmException {
        return eq(value.floatValue());
    }

    default Continuation<O> notEq(Long value) throws OrmException {
        return notEq(value.floatValue());
    }

    default Continuation<O> in(Long... values) throws OrmException {
        return in(Arrays.stream(values).map(Long::floatValue).collect(Collectors.toList()));
    }

    default Continuation<O> notIn(Long... values) throws OrmException {
        return notIn(Arrays.stream(values).map(Long::floatValue).collect(Collectors.toList()));
    }
}

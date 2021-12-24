package com.heliorm.def;

import com.heliorm.OrmException;
import com.heliorm.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A field representing a double value
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface DoubleField<T extends Table<O>, O> extends Field<T, O, Double>,  WithRange<T, O, Double>, WithEquals<T, O, Double>, WithIn<T, O, Double>, WithIs<T, O, Double> {

   default Continuation<T, O> lt(Integer value) throws OrmException {
        return lt(value.doubleValue());
    }

   default Continuation<T, O> le(Integer value) throws OrmException {
        return le(value.doubleValue());
    }

   default Continuation<T, O> gt(Integer value) throws OrmException {
        return gt(value.doubleValue());
    }

   default Continuation<T, O> ge(Integer value) throws OrmException {
        return ge(value.doubleValue());
    }

   default Continuation<T, O> eq(Integer value) throws OrmException {
        return eq(value.doubleValue());
    }

   default Continuation<T, O> notEq(Integer value) throws OrmException {
        return notEq(value.doubleValue());
    }

   default Continuation<T, O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }

   default Continuation<T, O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }

   default Continuation<T, O> lt(Long value) throws OrmException {
        return lt(value.doubleValue());
    }

   default Continuation<T, O> le(Long value) throws OrmException {
        return le(value.doubleValue());
    }

   default Continuation<T, O> gt(Long value) throws OrmException {
        return gt(value.doubleValue());
    }

   default Continuation<T, O> ge(Long value) throws OrmException {
        return ge(value.doubleValue());
    }

   default Continuation<T, O> eq(Long value) throws OrmException {
        return eq(value.doubleValue());
    }

   default Continuation<T, O> notEq(Long value) throws OrmException {
        return notEq(value.doubleValue());
    }

   default Continuation<T, O> in(Long... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }

   default Continuation<T, O> notIn(Long... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }
}

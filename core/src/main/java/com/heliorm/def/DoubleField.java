package com.heliorm.def;

import java.util.Arrays;
import java.util.stream.Collectors;
import com.heliorm.OrmException;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public interface DoubleField<T extends Table<O>, O> extends Field<T, O, Double>, Expression<T, O, Double>, WithRange<T, O, Double>, WithEquals<T, O, Double>, WithIn<T, O, Double>, WithIs<T, O, Double> {

   default ExpressionContinuation<T, O> lt(Integer value) throws OrmException {
        return lt(value.doubleValue());
    }

   default ExpressionContinuation<T, O> le(Integer value) throws OrmException {
        return le(value.doubleValue());
    }

   default ExpressionContinuation<T, O> gt(Integer value) throws OrmException {
        return gt(value.doubleValue());
    }

   default ExpressionContinuation<T, O> ge(Integer value) throws OrmException {
        return ge(value.doubleValue());
    }

   default ExpressionContinuation<T, O> eq(Integer value) throws OrmException {
        return eq(value.doubleValue());
    }

   default ExpressionContinuation<T, O> notEq(Integer value) throws OrmException {
        return notEq(value.doubleValue());
    }

   default ExpressionContinuation<T, O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }

   default ExpressionContinuation<T, O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }

   default ExpressionContinuation<T, O> lt(Long value) throws OrmException {
        return lt(value.doubleValue());
    }

   default ExpressionContinuation<T, O> le(Long value) throws OrmException {
        return le(value.doubleValue());
    }

   default ExpressionContinuation<T, O> gt(Long value) throws OrmException {
        return gt(value.doubleValue());
    }

   default ExpressionContinuation<T, O> ge(Long value) throws OrmException {
        return ge(value.doubleValue());
    }

   default ExpressionContinuation<T, O> eq(Long value) throws OrmException {
        return eq(value.doubleValue());
    }

   default ExpressionContinuation<T, O> notEq(Long value) throws OrmException {
        return notEq(value.doubleValue());
    }

   default ExpressionContinuation<T, O> in(Long... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }

   default ExpressionContinuation<T, O> notIn(Long... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.doubleValue()).collect(Collectors.toList()));
    }
}

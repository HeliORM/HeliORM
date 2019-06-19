package net.legrange.orm;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author gideon
 * @param <T>
 */
public interface LongField<T extends Table<O>, O> extends NumberField<T, O, Long> {

    public default ExpressionContinuation<T, O> lt(Integer value) throws OrmException {
        return lt(value.longValue());
    }

    public default ExpressionContinuation<T, O> le(Integer value) throws OrmException {
        return le(value.longValue());
    }

    public default ExpressionContinuation<T, O> gt(Integer value) throws OrmException {
        return gt(value.longValue());
    }

    public default ExpressionContinuation<T, O> ge(Integer value) throws OrmException {
        return ge(value.longValue());
    }

    public default ExpressionContinuation<T, O> eq(Integer value) throws OrmException {
        return eq(value.longValue());
    }

    public default ExpressionContinuation<T, O> notEq(Integer value) throws OrmException {
        return notEq(value.longValue());
    }

    public default ExpressionContinuation<T, O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.longValue()).collect(Collectors.toList()));
    }

    public default ExpressionContinuation<T, O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.longValue()).collect(Collectors.toList()));
    }

}

package net.legrange.orm.def;

import java.util.Arrays;
import java.util.stream.Collectors;
import net.legrange.orm.OrmException;
import net.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public interface FloatField<T extends Table<O>, O> extends NumberField<T, O, Float> {

    public default ExpressionContinuation<T, O> lt(Integer value) throws OrmException {
        return lt(value.floatValue());
    }

    public default ExpressionContinuation<T, O> le(Integer value) throws OrmException {
        return le(value.floatValue());
    }

    public default ExpressionContinuation<T, O> gt(Integer value) throws OrmException {
        return gt(value.floatValue());
    }

    public default ExpressionContinuation<T, O> ge(Integer value) throws OrmException {
        return ge(value.floatValue());
    }

    public default ExpressionContinuation<T, O> eq(Integer value) throws OrmException {
        return eq(value.floatValue());
    }

    public default ExpressionContinuation<T, O> notEq(Integer value) throws OrmException {
        return notEq(value.floatValue());
    }

    public default ExpressionContinuation<T, O> in(Integer... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.floatValue()).collect(Collectors.toList()));
    }

    public default ExpressionContinuation<T, O> notIn(Integer... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.floatValue()).collect(Collectors.toList()));
    }

    public default ExpressionContinuation<T, O> lt(Long value) throws OrmException {
        return lt(value.floatValue());
    }

    public default ExpressionContinuation<T, O> le(Long value) throws OrmException {
        return le(value.floatValue());
    }

    public default ExpressionContinuation<T, O> gt(Long value) throws OrmException {
        return gt(value.floatValue());
    }

    public default ExpressionContinuation<T, O> ge(Long value) throws OrmException {
        return ge(value.floatValue());
    }

    public default ExpressionContinuation<T, O> eq(Long value) throws OrmException {
        return eq(value.floatValue());
    }

    public default ExpressionContinuation<T, O> notEq(Long value) throws OrmException {
        return notEq(value.floatValue());
    }

    public default ExpressionContinuation<T, O> in(Long... values) throws OrmException {
        return in(Arrays.stream(values).map(value -> value.floatValue()).collect(Collectors.toList()));
    }

    public default ExpressionContinuation<T, O> notIn(Long... values) throws OrmException {
        return notIn(Arrays.stream(values).map(value -> value.floatValue()).collect(Collectors.toList()));
    }
}

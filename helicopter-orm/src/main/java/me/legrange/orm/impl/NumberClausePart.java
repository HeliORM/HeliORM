package me.legrange.orm.impl;

import java.util.Arrays;
import java.util.List;
import me.legrange.orm.Continuation;
import me.legrange.orm.Field;
import me.legrange.orm.NumberClause;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class NumberClausePart<T extends Table<O>, O, N extends Number, RT extends Table<RO>, RO> extends ClausePart<T, O, N, RT, RO> implements NumberClause<T, O, N, RT, RO> {

    public NumberClausePart(Part left, Operator op, Field<T, O, N> field) {
        super(left, op, field);
    }

    @Override
    public Continuation<T, O, RT, RO> lt(N value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> le(N value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> gt(N value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> ge(N value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> eq(N value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> notEq(N value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> in(List<N> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(List<N> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> in(N... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(N... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

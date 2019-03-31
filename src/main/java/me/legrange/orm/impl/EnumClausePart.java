package me.legrange.orm.impl;

import java.util.Arrays;
import java.util.List;
import me.legrange.orm.Continuation;
import me.legrange.orm.EnumClause;
import me.legrange.orm.Field;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class EnumClausePart<T extends Table<O>, O, E extends Enum, RT extends Table<RO>, RO> extends ClausePart<T, O, E, RT, RO> implements EnumClause<T, O, E, RT, RO> {

    public EnumClausePart(Part left, Operator op, Field<T, O, E> field) {
        super(left, op, field);
    }

    @Override
    public Continuation<T, O, RT, RO> eq(E value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> notEq(E value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> in(List<E> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(List<E> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> in(E... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(E... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, Arrays.asList(values));
    }

}

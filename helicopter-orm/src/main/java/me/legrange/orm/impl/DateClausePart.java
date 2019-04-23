package me.legrange.orm.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import me.legrange.orm.Continuation;
import me.legrange.orm.DateClause;
import me.legrange.orm.Field;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class DateClausePart<T extends Table<O>, O, RT extends Table<RO>, RO>
        extends ClausePart<T, O, Date, RT, RO> implements DateClause<T, O, RT, RO> {

    public DateClausePart(Part left, Operator op, Field<T, O, Date> field) {
        super(left, op, field);
    }

    @Override
    public Continuation<T, O, RT, RO> lt(Date value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> le(Date value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> gt(Date value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> ge(Date value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> eq(Date value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> notEq(Date value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> in(List<Date> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(List<Date> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> in(Date... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(Date... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, Arrays.asList(values));
    }

    @Override
    public Type getType() {
        return Type.CLAUSE;
    }
}

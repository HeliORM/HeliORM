package me.legrange.orm.impl;

import java.util.Arrays;
import java.util.List;
import me.legrange.orm.Continuation;
import me.legrange.orm.Field;
import me.legrange.orm.StringClause;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class StringClausePart<T extends Table<O>, O, RT extends Table<RO>, RO>
        extends ClausePart<T, O, String, RT, RO> implements StringClause<T, O, RT, RO> {

    public StringClausePart(Part left, Operator op, Field<T, O, String> field) {
        super(left, op, field);
    }

    @Override
    public Continuation<T, O, RT, RO> lt(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> le(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> gt(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> ge(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> eq(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> notEq(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> in(List<String> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(List<String> values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> in(String... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(String... values) {
        return new ListOperatorPart<>(this, ListOperatorPart.Operator.NOT_IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O, RT, RO> like(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LIKE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> norLike(String value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.NOT_LIKE, value);
    }

    @Override
    public Type getType() {
        return Type.CLAUSE;
    }
}

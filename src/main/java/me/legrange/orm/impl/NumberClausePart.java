/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.legrange.orm.impl;

import java.util.Arrays;
import java.util.List;
import me.legrange.orm.Continuation;
import me.legrange.orm.NumberClause;
import me.legrange.orm.NumberField;
import me.legrange.orm.Table;

class NumberClausePart<T extends Table<O>, O, C extends Number, RT extends Table<RO>, RO> extends Part implements NumberClause<T, O, C, RT, RO> {

    private final NumberField<T, O, C> field;

    NumberClausePart(Part left, NumberField<T, O, C> field) {
        super(left);
        this.field = field;

    }

    @Override
    public Continuation<T, O, RT, RO> lt(C value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> le(C value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.LE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> gt(C value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GT, value);
    }

    @Override
    public Continuation<T, O, RT, RO> ge(C value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.GE, value);
    }

    @Override
    public Continuation<T, O, RT, RO> eq(C value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> notEq(C value) {
        return new ValueOperatorPart(this, ValueOperatorPart.Operator.NOT_EQ, value);
    }

    @Override
    public Continuation<T, O, RT, RO> in(List<C> values) {
        return new ListOperatorPart(this, ListOperatorPart.Operator.IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(List<C> values) {
        return new ListOperatorPart(this, ListOperatorPart.Operator.NOT_IN, values);
    }

    @Override
    public Continuation<T, O, RT, RO> in(C... values) {
        return new ListOperatorPart(this, ListOperatorPart.Operator.IN, Arrays.asList(values));
    }

    @Override
    public Continuation<T, O, RT, RO> notIn(C... values) {
        return new ListOperatorPart(this, ListOperatorPart.Operator.NOT_IN, Arrays.asList(values));
    }

    @Override
    protected String query() {
        return left.query();
    }

}

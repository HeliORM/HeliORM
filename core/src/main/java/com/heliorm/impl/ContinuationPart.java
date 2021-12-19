package com.heliorm.impl;

import com.heliorm.FieldOrder;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.Executable;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Join;

import static java.lang.String.format;

public final class ContinuationPart<DT extends Table<DO>, DO, LT extends Table<LO>, LO> extends ExecutablePart<DT, DO> implements Continuation<DT, DO, LT, LO> {

    private final Part expression;

    ContinuationPart(Part left, Type type, ExpressionContinuation expr) {
        super(type, left);
        this.expression = ((Part) expr).head();
    }

    @Override
    public Continuation<DT, DO, LT, LO> and(ExpressionContinuation<LT, LO> cont) {
        return new ContinuationPart(this, Type.AND, cont);
    }

    @Override
    public Continuation<DT, DO, LT, LO> or(ExpressionContinuation<LT, LO> cont) {
        return new ContinuationPart(this, Type.OR, cont);
    }

    @Override
    public <RT extends Table<RO>, RO> Join<DT, DO, LT, LO, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public <F extends FieldOrder<DT, DO, ?>> Executable<DT, DO> orderBy(F order, F...orders) {
        OrderedPart<DT, DO>  part = order(this, order);
        for (F o : orders) {
            part = order(part, o);
        }
        return part;
    }


    private <F extends FieldOrder<DT, DO, ?>> OrderedPart<DT, DO> order(Part left, F order) {
       return new OrderedPart(this,
               order.getDirection() == FieldOrder.Direction.ASC ?  OrderedPart.Direction.ASCENDING : OrderedPart.Direction.DESCENDING,
               (FieldPart) (order.getField()));
    }


    public Part getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return format("%s %s", getType().name(), getExpression());
    }

}

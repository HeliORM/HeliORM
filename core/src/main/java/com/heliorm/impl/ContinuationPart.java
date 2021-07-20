package com.heliorm.impl;

import com.heliorm.FieldOrder;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.Executable;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Join;

import static java.lang.String.format;

public final class ContinuationPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends ExecutablePart<LT, LO> implements Continuation<LT, LO, RT, RO> {

    private final Part expression;

    ContinuationPart(Part left, Type type, ExpressionContinuation expr) {
        super(type, left);
        this.expression = ((Part) expr).head();
    }

    @Override
    public Continuation<LT, LO, RT, RO> and(ExpressionContinuation<RT, RO> cont) {
        return new ContinuationPart(this, Type.AND, cont);
    }

    @Override
    public Continuation<LT, LO, RT, RO> or(ExpressionContinuation<RT, RO> cont) {
        return new ContinuationPart(this, Type.OR, cont);
    }

    @Override
    public <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public <F extends FieldOrder<LT, LO, ?>> Executable<LT, LO> orderBy(F order, F...orders) {
        OrderedPart<LT, LO>  part = order(this, order);
        for (F o : orders) {
            part = order(part, o);
        }
        return part;
    }


    private <F extends FieldOrder<LT, LO, ?>> OrderedPart<LT, LO> order(Part left, F order) {
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

package com.heliorm.impl;

import com.heliorm.FieldOrder;
import com.heliorm.Table;
import com.heliorm.def.Executable;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Join;
import com.heliorm.def.OnContinuation;

import static java.lang.String.format;

public abstract class AbstractContinuationPart<DT extends Table<DO>, DO> extends ExecutablePart<DT, DO> {

    private final Part expression;

    AbstractContinuationPart(Part left, Type type, ExpressionContinuation expr) {
        super(type, left);
        this.expression = ((Part) expr).head();
    }


    private <F extends FieldOrder<DT, DO, ?>> OrderedPart<DT, DO> order(Part left, F order) {
        return new OrderedPart(this,
                order.getDirection() == FieldOrder.Direction.ASC ?  OrderedPart.Direction.ASCENDING : OrderedPart.Direction.DESCENDING,
                (FieldPart) (order.getField()));
    }


    public final Part getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return format("%s %s", getType().name(), getExpression());
    }

}

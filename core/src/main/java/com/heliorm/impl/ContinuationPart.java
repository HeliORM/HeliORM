package com.heliorm.impl;

import static java.lang.String.format;
import com.heliorm.def.Continuation;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Field;
import com.heliorm.def.Join;
import com.heliorm.def.Ordered;
import com.heliorm.OrmException;
import com.heliorm.Table;

public class ContinuationPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends ExecutablePart<LT, LO> implements Continuation<LT, LO, RT, RO> {

    private final Part expression;
    private final Type type;

    ContinuationPart(Part left, Type type, ExpressionContinuation expr) {
        super(left);
        this.expression = ((Part) expr).head();
        this.type = type;
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
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderBy(F field) {
        return new OrderedPart(this, OrderedPart.Direction.ASCENDING, field);
    }

    @Override
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderByDesc(F field) {
        return new OrderedPart(this, OrderedPart.Direction.DESCENDING, field);
    }

    @Override
    public Type getType() {
        return type;
    }

    public Part getExpression() throws OrmException {
        return expression;
    }

    @Override
    public String toString() {
        return format("%s %s", getType().name(), expression);
    }

}

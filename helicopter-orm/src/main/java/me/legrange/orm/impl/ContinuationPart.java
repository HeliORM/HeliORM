package me.legrange.orm.impl;

import me.legrange.orm.Continuation;
import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.Field;
import me.legrange.orm.Join;
import me.legrange.orm.Ordered;
import me.legrange.orm.OrmException;
import me.legrange.orm.Table;

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
        return getType().name();
    }

}

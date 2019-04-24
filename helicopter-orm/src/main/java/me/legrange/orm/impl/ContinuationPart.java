package me.legrange.orm.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.legrange.orm.Continuation;
import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.Field;
import me.legrange.orm.Join;
import me.legrange.orm.Ordered;
import me.legrange.orm.OrmException;
import me.legrange.orm.Table;

public class ContinuationPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Part implements Continuation<LT, LO, RT, RO> {

    private final ExpressionContinuation cont;
    private final Type type;

    ContinuationPart(Part left, Type type, ExpressionContinuation cont) {
        super(left);
        this.cont = cont;
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
    public List<LO> list() throws OrmException {
        return getOrm().list(this);
    }

    @Override
    public Stream<LO> stream() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LO one() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<LO> oneOrNone() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

}

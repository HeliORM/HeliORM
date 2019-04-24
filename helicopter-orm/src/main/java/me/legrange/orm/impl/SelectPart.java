package me.legrange.orm.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.legrange.orm.Continuation;
import me.legrange.orm.ExpressionContinuation;
import me.legrange.orm.Field;
import me.legrange.orm.Join;
import me.legrange.orm.Ordered;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import me.legrange.orm.Select;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class SelectPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Part<LT, LO, RT, RO>
        implements Select<LT, LO, RT, RO> {

    private final Orm orm;
    private final Table table;

    public SelectPart(Part left, Table table) {
        this(left, table, null);
    }

    public SelectPart(Part left, Table table, Orm orm) {
        super(left);
        this.table = table;
        this.orm = orm;
    }

    @Override
    protected Orm getOrm() {
        if (getLeft() != null) {
            return getLeft().getOrm();
        }
        return orm;
    }

    @Override
    public final Table getReturnTable() {
        if (getLeft() != null) {
            return getLeft().getReturnTable();
        }
        return table;
    }

    @Override
    public Table getSelectTable() {
        return table;
    }

    @Override
    public <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public Continuation<LT, LO, RT, RO> where(ExpressionContinuation<RT, RO> cont) {
        return new ContinuationPart(this, Type.WHERE, cont);
    }

    @Override
    public List<LO> list() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return Type.SELECT;
    }

}

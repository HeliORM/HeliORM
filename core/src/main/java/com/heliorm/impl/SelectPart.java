package com.heliorm.impl;

import static java.lang.String.format;
import com.heliorm.def.Continuation;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Field;
import com.heliorm.def.Join;
import com.heliorm.def.Ordered;
import com.heliorm.Orm;
import com.heliorm.def.Select;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class SelectPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends ExecutablePart<LT, LO>
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
        if (left() != null) {
            return left().getOrm();
        }
        return orm;
    }

    @Override
    public final Table getReturnTable() {
        if (left() != null) {
            return left().getReturnTable();
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

    @Override
    public String toString() {
        return format("%s %s", getType().name(), table.getSqlTable());
    }

}
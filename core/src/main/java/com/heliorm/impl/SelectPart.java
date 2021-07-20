package com.heliorm.impl;

import com.heliorm.FieldOrder;
import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.Executable;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Join;
import com.heliorm.def.Select;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class SelectPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends ExecutablePart<LT, LO>
        implements Select<LT, LO, RT, RO> {

    private final Selector selector;
    private final Table table;

    public SelectPart(Type type, Part left, Table table) {
        super(type, left);
        this.selector = null;
        this.table = table;
    }

    public SelectPart(Part left, Table table) {
        this(left, table, null);
    }

    public SelectPart(Part left, Table table, Selector orm) {
        super(Type.SELECT, left);
        this.table = table;
        this.selector = orm;
    }

    @Override
    protected Selector getSelector() {
        if (left() != null) {
            return left().getSelector();
        }
        return selector;
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
    public <F extends FieldOrder<LT, LO, ?>> Executable<LT, LO> orderBy(F order, F...orders) {
        OrderedPart<LT, LO>  part = order(this, order);
        for (F o : orders) {
            part = order(part, o);
        }
        return part;
    }

    private <F extends FieldOrder<LT, LO, ?>> OrderedPart<LT, LO> order(Part left, F order) {
        return new OrderedPart(left,
                order.getDirection() == FieldOrder.Direction.ASC ?  OrderedPart.Direction.ASCENDING : OrderedPart.Direction.DESCENDING,
                (FieldPart) (order.getField()));
    }

    @Override
    public String toString() {
        return format("%s %s", getType().name(), table.getSqlTable());
    }

}

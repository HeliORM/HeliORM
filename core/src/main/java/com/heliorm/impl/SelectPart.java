package com.heliorm.impl;

import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Complete;
import com.heliorm.def.Executable;
import com.heliorm.def.FieldOrder;
import com.heliorm.def.Select;
import com.heliorm.def.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * @author gideon
 */
public final class SelectPart<DO> extends ExecutablePart<DO> implements Select<DO> {

    private final Selector selector;
    private final Table<DO> table;
    private final Where<DO> where;
    private final List<JoinPart<?,?>> joins;
    private List<OrderPart<DO>> order;
    private LimitPart limit;

    public SelectPart(Selector orm, Table<DO> table) {
        this(orm, table, null,  Collections.emptyList());
    }

    public SelectPart(Selector orm, Table<DO> table, Where<DO> where, List<JoinPart<?, ?>> joins, List<OrderPart<DO>> order, LimitPart limit) {
        super(orm);
        this.table = table;
        this.where = where;
        this.selector = orm;
        this.joins = joins;
        this.order = order;
        this.limit = limit;
    }

    public SelectPart(Selector orm, Table<DO> table, Where<DO> where, List<JoinPart<?, ?>> joins) {
        this(orm, table, where, joins, Collections.emptyList(), new LimitPart(-1, -1));
    }


    public Selector getSelector() {
        return selector;
    }


    public Table<DO> getTable() {
        return table;
    }

    @Override
    public <F extends FieldOrder<DO, ?>> Complete<DO> orderBy(F order, F... orders) {
        List<OrderPart<DO>> list = new ArrayList<>();
        list.add(makePart(order));
        for (F o : orders) {
            list.add(makePart(o));
        }
        this.order = list;
        return new OrderedPart(getSelector(), this, list, limit);
    }

    @Override
    public SelectPart<DO> getSelect() {
        return this;
    }

    @Override
    public List<OrderPart<DO>> getOrder() {
        return order == null ? Collections.EMPTY_LIST : order;
    }

    @Override
    public LimitPart getLimit() {
        return limit;
    }

    void setLimit(LimitPart limit) {
        this.limit = limit;
    }

    @Override
    public List<DO> list() throws OrmException {
        return getSelector().list(this);
    }

    @Override
    public Stream<DO> stream() throws OrmException {
        return getSelector().stream(this);
    }

    @Override
    public DO one() throws OrmException {
        return getSelector().one(this);
    }

    @Override
    public Optional<DO> optional() throws OrmException {
        return getSelector().optional(this);
    }

    public Optional<Where< DO>> getWhere() {
        return Optional.ofNullable(where);
    }

    public List<JoinPart<?, ?>> getJoins() {
        return joins;
    }

    @Override
    public String toString() {
        return format("SELECT %s", table.getSqlTable());
    }

    private <F extends FieldOrder<DO, ?>> OrderPart<DO> makePart(F order) {
        return new OrderPart(order.getDirection() == FieldOrder.Direction.ASC ? OrderPart.Direction.ASCENDING : OrderPart.Direction.DESCENDING,
                (FieldPart) (order.getField()));
    }

    @Override
    public Executable<DO> limit(int from, int number) {
        limit = new LimitPart(from, number);
        return this;
    }

    @Override
    public Executable<DO> limit(int number) {
        limit = new LimitPart(number);
        return this;
    }
}

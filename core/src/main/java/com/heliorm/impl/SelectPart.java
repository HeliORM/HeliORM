package com.heliorm.impl;

import com.heliorm.def.FieldOrder;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.def.Where;
import com.heliorm.def.Executable;
import com.heliorm.def.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * @author gideon
 */
public class SelectPart<DT extends Table<DO>, DO> extends ExecutablePart<DT, DO> implements Select<DT, DO> {

    private final Selector selector;
    private final DT table;
    private final Optional<Where<DT,DO>> where;
    private final List<JoinPart<?,?,?,?>> joins;
    private  List<OrderPart<DT,DO>> order;

    public SelectPart(Selector orm, DT table) {
        this(orm, table, Optional.empty(), Collections.EMPTY_LIST);
    }

    public SelectPart(Selector orm, DT table, Optional<Where<DT, DO>> where,  List<JoinPart<?,?,?,?>> joins, List<OrderPart<DT,DO>> order) {
        super(orm);
        this.table = table;
        this.where = where;
        this.selector = orm;
        this.joins = joins;
        this.order = order;
    }

    public SelectPart(Selector orm, DT table, Optional<Where<DT, DO>> where,  List<JoinPart<?,?,?,?>> joins) {
        this(orm,table,where, joins, Collections.EMPTY_LIST);
    }


    public Selector getSelector() {
        return selector;
    }


    public Table getTable() {
        return table;
    }

    @Override
    public <F extends FieldOrder<DT, DO, ?>> Executable<DO> orderBy(F order, F... orders) {
        List<OrderPart<DT,DO>> list =new ArrayList<>();
        list.add(makePart(order));
        for (F o : orders) {
            list.add(makePart(o));
        }
        this.order = list;
        return new OrderedPart<>(getSelector(), this, list);
    }

    @Override
    public SelectPart<DT, DO> getSelect() {
        return this;
    }

    @Override
    public List<OrderPart<DT,DO>> getOrder() {
        return order == null ? Collections.EMPTY_LIST : order;
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

    public Optional<Where<DT, DO>> getWhere() {
        return where;
    }

    public List<JoinPart<?,?,?,?>> getJoins() {
        return joins;
    }

    @Override
    public String toString() {
        return format("SELECT %s", table.getSqlTable());
    }


    private  <F extends FieldOrder<DT, DO, ?>> OrderPart<DT, DO> makePart(F order) {
        return new OrderPart(order.getDirection() == FieldOrder.Direction.ASC ? OrderPart.Direction.ASCENDING : OrderPart.Direction.DESCENDING,
                (FieldPart) (order.getField()));
    }
}

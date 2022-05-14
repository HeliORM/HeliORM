package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Complete;
import com.heliorm.def.Executable;

import java.util.List;

/**
 * @author gideon
 */
public final class OrderedPart<DT extends Table<DO>, DO> extends ExecutablePart<DT, DO> implements Complete<DO> {


    private final SelectPart<DT, DO> select;
    private final List<OrderPart<DT, DO>> order;

    public OrderedPart(Selector selector, SelectPart<DT, DO> select, List<OrderPart<DT, DO>> order, LimitPart<DO> limit) {
        super(selector);
        this.select = select;
        this.order = order;
        this.select.setLimit(limit);
    }

    @Override
    public SelectPart<DT, DO> getSelect() {
        return select;
    }

    @Override
    public List<OrderPart<DT, DO>> getOrder() {
        return order;
    }

    @Override
    public LimitPart<DO> getLimit() {
        return select.getLimit();
    }

    @Override
    public Executable<DO> limit(int from, int number) {
        this.select.setLimit(new LimitPart<>(from, number));
        return this;
    }

    @Override
    public Executable<DO> limit(int number) {
        return limit(0, number);
    }
}

package com.heliorm.impl;

import com.heliorm.def.Complete;
import com.heliorm.def.Executable;

import java.util.List;

/**
 * @author gideon
 */
public final class OrderedPart< DO> extends ExecutablePart<DO> implements Complete<DO> {


    private final SelectPart< DO> select;
    private final List<OrderPart< DO>> order;

    public OrderedPart(Selector selector, SelectPart< DO> select, List<OrderPart< DO>> order, LimitPart limit) {
        super(selector);
        this.select = select;
        this.order = order;
        this.select.setLimit(limit);
    }

    @Override
    public SelectPart<DO> getSelect() {
        return select;
    }

    @Override
    public List<OrderPart< DO>> getOrder() {
        return order;
    }

    @Override
    public LimitPart getLimit() {
        return select.getLimit();
    }

    @Override
    public Executable<DO> limit(int from, int number) {
        this.select.setLimit(new LimitPart(from, number));
        return this;
    }

    @Override
    public Executable<DO> limit(int number) {
        return limit(0, number);
    }
}

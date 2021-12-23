package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Executable;

import java.util.List;

/**
 *
 * @author gideon
 */
public final class OrderedPart<DT extends Table<DO>, DO> extends ExecutablePart<DT, DO> implements  Executable<DO> {


    private final SelectPart<DT,DO> select;
    private final List<OrderPart<DT,DO>> order;

    public OrderedPart(Selector selector, SelectPart<DT,DO> select, List<OrderPart<DT,DO>>  order) {
        super(selector);
        this.select =select;
        this.order = order;
    }

    @Override
    public SelectPart<DT, DO> getSelect() {
        return select;
    }

    @Override
    public List<OrderPart<DT, DO>> getOrder() {
        return order;
    }
}

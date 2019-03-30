package me.legrange.orm.impl;

import java.util.List;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
class ListOperatorPart<T extends Table<O>, O, C, RT extends Table<RO>, RO> extends ContinuationPart<T, O, RT, RO> {

    enum Operator {
        IN, NOT_IN;

    }

    private final Operator op;
    private final List<C> values;

    ListOperatorPart(Part left, Operator op, List<C> values) {
        super(left);
        this.op = op;
        this.values = values;
    }

}

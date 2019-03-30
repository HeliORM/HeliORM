package me.legrange.orm.impl;

import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
class ValueOperatorPart<T extends Table<O>, O, C, RT extends Table<RO>, RO> extends ContinuationPart<T, O, RT, RO> {

    enum Operator {
        EQ, NOT_EQ, LIKE, NOT_LIKE, LT, GT, LE, GE, IN, NOT_IN;

    }

    private final Operator op;
    private final C value;

    ValueOperatorPart(Part left, Operator op, C value) {
        super(left);
        this.op = op;
        this.value = value;
    }

}

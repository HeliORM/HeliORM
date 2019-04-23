package me.legrange.orm.impl;

import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public final class ValueOperatorPart<T extends Table<O>, O, C, RT extends Table<RO>, RO> extends ContinuationPart<T, O, RT, RO> {

    public enum Operator {
        EQ, NOT_EQ, LIKE, NOT_LIKE, LT, GT, LE, GE;

    }

    private final Operator op;
    private final C value;

    ValueOperatorPart(Part left, Operator op, C value) {
        super(left);
        this.op = op;
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.VALUE_OPERATION;
    }

    public Operator getOp() {
        return op;
    }

    public C getValue() {
        return value;
    }

}

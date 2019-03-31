package me.legrange.orm.impl;

import me.legrange.orm.Field;
import me.legrange.orm.Table;

public abstract class ClausePart<T extends Table<O>, O, C, RT extends Table<RO>, RO> extends Part {

    public enum Operator {
        WHERE, AND, OR;
    }

    private final Field<T, O, C> field;
    private final Operator operator;

    protected ClausePart(Part left, Operator op, Field<T, O, C> field) {
        super(left);
        this.field = field;
        this.operator = op;
    }

    public Field<T, O, C> getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public Type getType() {
        return Type.CLAUSE;
    }
}

package com.heliorm.impl;

import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public abstract class Part<DT extends Table<DO>, DO, LT extends Table<LO>, LO> {

    public enum Type {
        SELECT,
        WHERE,
        AND,
        OR,
        FIELD,
        VALUE_EXPRESSION,
        LIST_EXPRESSION,
        IS_EXPRESSION,
        JOIN,
        ORDER;
    }

    private final Type type;
    private final Part left;
    private Part right;

    protected Part(Type type, Part left) {
        this.type = type;
        this.left = left;
        if (left != null) {
            left.setRight(this);
        }
    }

    protected Selector getSelector() {
        return left.getSelector();
    }

    public final Type getType() {
        return type;
    }

    public Table getReturnTable() {
        return left.getReturnTable();
    }

    public Table getSelectTable() {
        return left.getSelectTable();
    }

    public Part left() {
        return left;
    }

    public final Part right() {
        return right;
    }

    public final Part head() {
        if (left != null) {
            return left.head();
        }
        return this;
    }

    protected final void setRight(Part right) {

        this.right = right;
    }

    @Override
    public abstract String toString();

}

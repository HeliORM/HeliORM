package com.heliorm.impl;

import com.heliorm.Table;

public class OrderPart<T extends Table<O>, O> {
    public enum Direction {
        ASCENDING, DESCENDING;
    }

    private final Direction direction;
    private final FieldPart<T,O,?> field;

    public OrderPart(Direction direction, FieldPart<T, O, ?> field) {
        this.direction = direction;
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public FieldPart<T, O, ?> getField() {
        return field;
    }
}

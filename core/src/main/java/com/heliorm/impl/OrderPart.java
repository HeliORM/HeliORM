package com.heliorm.impl;

public final class OrderPart<O> {
    private final Direction direction;
    private final FieldPart< O, ?> field;
    public OrderPart(Direction direction, FieldPart< O, ?> field) {
        this.direction = direction;
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public FieldPart<O, ?> getField() {
        return field;
    }

    public enum Direction {
        ASCENDING, DESCENDING
    }
}

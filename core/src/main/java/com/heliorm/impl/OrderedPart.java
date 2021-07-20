package com.heliorm.impl;

import com.heliorm.def.Executable;
import com.heliorm.def.Field;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class OrderedPart<T extends Table<O>, O> extends ExecutablePart<T, O> implements  Executable<T, O> {

    public enum Direction {
        ASCENDING, DESCENDING;
    }

    private final Direction direction;
    private final FieldPart<T, O, ?> field;

    public OrderedPart(Part left, Direction direction, FieldPart<T, O, ?> field) {
        super(Type.ORDER, left);
        this.direction = direction;
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public Field<T, O, ?> getField() {
        return field;
    }

    @Override
    public String toString() {
        return getType().name();
    }

}

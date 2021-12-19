package com.heliorm.impl;

import com.heliorm.def.Executable;
import com.heliorm.def.Field;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public final class OrderedPart<DT extends Table<DO>, DO> extends ExecutablePart<DT, DO> implements  Executable<DT, DO> {

    public enum Direction {
        ASCENDING, DESCENDING;
    }

    private final Direction direction;
    private final FieldPart<DT, DO, ?> field;

    public OrderedPart(Part left, Direction direction, FieldPart<DT, DO, ?> field) {
        super(Type.ORDER, left);
        this.direction = direction;
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public Field<DT, DO, ?> getField() {
        return field;
    }

    @Override
    public String toString() {
        return getType().name();
    }

}

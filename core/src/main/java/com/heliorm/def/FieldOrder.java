package com.heliorm.def;

import com.heliorm.Field;

public interface FieldOrder<DO, C> {

    default Direction getDirection() {
        return Direction.ASC;
    }

    Field<DO, C> getField();

    enum Direction {
        ASC, DESC
    }

}

package com.heliorm;

import com.heliorm.def.Field;

public interface FieldOrder<T extends Table<O>, O, C> {

    enum Direction {
        ASC, DESC;
    }

    default Direction getDirection() {
        return Direction.ASC;
    }

    Field<T,O,C> getField();

}

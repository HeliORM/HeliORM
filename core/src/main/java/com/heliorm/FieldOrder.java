package com.heliorm;

import com.heliorm.def.Field;

public interface FieldOrder<DT extends Table<DO>, DO, C> {

    enum Direction {
        ASC, DESC;
    }

    default Direction getDirection() {
        return Direction.ASC;
    }

    Field<DT, DO,C> getField();

}

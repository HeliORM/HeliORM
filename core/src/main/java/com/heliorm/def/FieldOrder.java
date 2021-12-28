package com.heliorm.def;

import com.heliorm.Field;
import com.heliorm.Table;

public interface FieldOrder<DT extends Table<DO>, DO, C> {

    enum Direction {
        ASC, DESC;
    }

    default Direction getDirection() {
        return Direction.ASC;
    }

    Field<DT, DO,C> getField();

}

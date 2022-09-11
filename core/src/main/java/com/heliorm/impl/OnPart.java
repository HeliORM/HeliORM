package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;
import com.heliorm.def.On;

public class OnPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> implements On<LT, LO, RT, RO> {

    private final Field<LO, ?> leftField;
    private final Field<RO, ?> rightField;

    public OnPart(Field<LO, ?> leftField, Field<RO, ?> rightField) {
        this.leftField = leftField;
        this.rightField = rightField;
    }

    public Field<LO, ?> getLeftField() {
        return leftField;
    }

    public Field<RO, ?> getRightField() {
        return rightField;
    }
}

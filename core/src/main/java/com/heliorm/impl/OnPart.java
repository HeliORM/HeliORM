package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.def.On;

public final class OnPart<LO, RO> implements On< LO, RO> {

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

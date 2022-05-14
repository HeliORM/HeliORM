package com.heliorm.impl;

import com.heliorm.Field;
import com.heliorm.Table;
import com.heliorm.def.On;

public class OnPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> implements On<LT, LO, RT, RO> {

    private final Field<LT, LO, ?> leftField;
    private final Field<RT, RO, ?> rightField;

    public OnPart(Field<LT, LO, ?> leftField, Field<RT, RO, ?> rightField) {
        this.leftField = leftField;
        this.rightField = rightField;
    }

    public Field<LT, LO, ?> getLeftField() {
        return leftField;
    }

    public Field<RT, RO, ?> getRightField() {
        return rightField;
    }
}

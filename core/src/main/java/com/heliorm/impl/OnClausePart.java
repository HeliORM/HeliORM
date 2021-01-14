package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.def.OnClause;

import static java.lang.String.format;

public final class OnClausePart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends SelectPart<LT, LO, RT, RO> implements OnClause<LT, LO, RT, RO> {

    private final Field<LT, LO, ?> leftField;
    private final Field<RT, RO, ?> rightField;

    <C> OnClausePart(Part left, Field<LT, LO, C> leftField, Field<RT, RO, C> rightField) {
        super(Type.ON_CLAUSE, left, left.getSelectTable());
        this.leftField = leftField;
        this.rightField = rightField;
    }

    public Field<LT, LO, ?> getLeftField() {
        return leftField;
    }

    public Field<RT, RO, ?> getRightField() {
        return rightField;
    }

    @Override
    public String toString() {
        return format("ON %s = %s", leftField.getSqlName(), rightField.getSqlName());
    }

}

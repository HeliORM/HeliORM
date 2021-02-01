package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.def.OnClause;

import static java.lang.String.format;

public final class OnClausePart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends SelectPart<LT, LO, RT, RO> implements OnClause<LT, LO, RT, RO> {

    private final FieldPart<LT, LO, ?> leftField;
    private final FieldPart<RT, RO, ?> rightField;

    <C> OnClausePart(Part left, FieldPart<LT, LO, C> leftField, FieldPart<RT, RO, C> rightField) {
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

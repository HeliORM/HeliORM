package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.def.OnClause;

import static java.lang.String.format;

public final class OnClausePart<DT extends Table<DO>, DO, LT extends Table<LO>, LO> extends SelectPart<DT, DO, LT, LO> implements OnClause<DT, DO, LT, LO> {

    private final FieldPart<DT, DO, ?> leftField;
    private final FieldPart<LT, LO, ?> rightField;

    <C> OnClausePart(Part left, FieldPart<DT, DO, C> leftField, FieldPart<LT, LO, C> rightField) {
        super(Type.ON_CLAUSE, left, left.getSelectTable());
        this.leftField = leftField;
        this.rightField = rightField;
    }

    public Field<DT, DO, ?> getLeftField() {
        return leftField;
    }

    public Field<LT, LO, ?> getRightField() {
        return rightField;
    }

    @Override
    public String toString() {
        return format("ON %s = %s", leftField.getSqlName(), rightField.getSqlName());
    }

}

package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.def.Join;
import com.heliorm.def.OnClause;

import static java.lang.String.format;

public final class OnClausePart<DT extends Table<DO>, DO, LT extends Table<LO>, LO, RT extends Table<RO>, RO>
        extends SelectPart<DT, DO, RT, RO> implements OnClause<DT, DO, LT, LO, RT, RO> {

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
    public <NT extends Table<NO>, NO> Join<DT, DO, RT, RO, NT, NO> thenJoin(NT table) {
        return new JoinPart<DT, DO, RT, RO, NT, NO>(this, table);
    }

    @Override
    public String toString() {
        return format("ON %s = %s", leftField.getSqlName(), rightField.getSqlName());
    }

}

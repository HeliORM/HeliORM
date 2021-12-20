package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Continuation;
import com.heliorm.def.ExpressionContinuation;
import com.heliorm.def.Field;
import com.heliorm.def.Join;
import com.heliorm.def.OnClause;
import com.heliorm.def.OnContinuation;

import static java.lang.String.format;

public final class OnClausePart<DT extends Table<DO>, DO, LT extends Table<LO>, LO, RT extends Table<RO>, RO>
        extends ExecutablePart<DT, DO> implements OnClause<DT, DO, LT, LO, RT, RO> {

    private final FieldPart<DT, DO, ?> leftField;
    private final FieldPart<LT, LO, ?> rightField;

    <C> OnClausePart(Part left, FieldPart<DT, DO, C> leftField, FieldPart<LT, LO, C> rightField) {
        super(Type.ON_CLAUSE, left);
        this.leftField = leftField;
        this.rightField = rightField;
    }

    public Field<DT, DO, ?> getLeftField() {
        return leftField;
    }

    public Field<LT, LO, ?> getRightField() {
        return rightField;
    }

    public <NT extends Table<NO>, NO> Join<DT, DO, LT, LO, NT, NO> join(NT table) {
        return new JoinPart<>(this,table);
    }

    @Override
    public OnContinuation<DT, DO, RT, RO> where(ExpressionContinuation<RT, RO> cont) {
        return new OnContinuationPart(this, Type.WHERE, cont);
    }

    @Override
    public <NT extends Table<NO>, NO> Join<DT, DO, RT, RO, NT, NO> thenJoin(NT table) {
        return new JoinPart<>(this, table);
    }

    @Override
    public String toString() {
        return format("ON %s = %s", leftField.getSqlName(), rightField.getSqlName());
    }

}

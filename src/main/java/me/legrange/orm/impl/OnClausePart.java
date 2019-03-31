package me.legrange.orm.impl;

import me.legrange.orm.Field;
import me.legrange.orm.OnClause;
import me.legrange.orm.Table;

public final class OnClausePart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends SelectPart<LT, LO, RT, RO> implements OnClause<LT, LO, RT, RO> {

    private final Field<LT, LO, ?> leftField;
    private final Field<RT, RO, ?> rightField;

    <C> OnClausePart(Part left, Field<LT, LO, C> leftField, Field<RT, RO, C> rightField) {
        super(left, null);
        this.leftField = leftField;
        this.rightField = rightField;
    }

    @Override
    public Type getType() {
        return Type.ON_CLAUSE;
    }

    public Field<LT, LO, ?> getLeftField() {
        return leftField;
    }

    public Field<RT, RO, ?> getRightField() {
        return rightField;
    }

}

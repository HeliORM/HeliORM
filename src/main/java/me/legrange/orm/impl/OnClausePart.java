package me.legrange.orm.impl;

import me.legrange.orm.Field;
import me.legrange.orm.OnClause;
import me.legrange.orm.Select;
import me.legrange.orm.Table;

class OnClausePart<LT extends Table<LO>, LO, RT extends Table<RO>, RO, C> extends Part implements OnClause<LT, LO, RT, RO, C> {

    private final Field<LT, LO, C> field;

    OnClausePart(Part left, Field<LT, LO, C> field) {
        super(left);
        this.field = field;
    }

    @Override
    public <F extends Field<RT, RO, C>, C> Select<RT, RO, LT, LO> eq(F field) {
        return new SelectPart(left, null);
    }

    @Override
    protected String query() {
        return left.query() + String.format(" %s", field.getSqlField());
    }

}

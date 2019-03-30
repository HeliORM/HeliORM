package me.legrange.orm.impl;

import static java.lang.String.format;
import me.legrange.orm.Field;
import me.legrange.orm.Join;
import me.legrange.orm.OnClause;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
class JoinPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Part<LT, LO, RT, RO> implements Join<LT, LO, RT, RO> {

    private final RT joinTable;

    JoinPart(Part left, RT joinTable) {
        super(left);
        this.joinTable = joinTable;
    }

    @Override
    public <F extends Field<LT, LO, C>, C> OnClause<LT, LO, RT, RO, C> on(F field) {
        return new OnClausePart(this, field);
    }

    @Override
    protected String query() {
        return left.query() + format(" join %s", joinTable.getSqTable());
    }

}

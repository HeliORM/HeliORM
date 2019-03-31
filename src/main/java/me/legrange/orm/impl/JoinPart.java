package me.legrange.orm.impl;

import me.legrange.orm.Field;
import me.legrange.orm.Join;
import me.legrange.orm.OnClause;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class JoinPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Part<LT, LO, RT, RO> implements Join<LT, LO, RT, RO> {

    private final RT table;

    JoinPart(Part left, RT table) {
        super(left);
        this.table = table;
    }

    @Override
    public <F extends Field<LT, LO, C>, C> OnClause<LT, LO, RT, RO, C> on(F field) {
        return new OnClausePart(this, field);
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }

    public RT getTable() {
        return table;
    }

}

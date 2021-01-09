package com.heliorm.impl;

import static java.lang.String.format;

import com.heliorm.def.Join;
import com.heliorm.def.Field;
import com.heliorm.def.OnClause;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public class JoinPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Part<LT, LO, RT, RO> implements Join<LT, LO, RT, RO> {

    private final RT table;

    public JoinPart(Part left, RT table) {
        super(left);
        this.table = table;
    }

    @Override
    public <L extends Field<LT, LO, C>, R extends Field<RT, RO, C>, C> OnClause<LT, LO, RT, RO> on(L leftField, R rightField) {
        return new OnClausePart(this, leftField, rightField);
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }

    @Override
    public Table getSelectTable() {
        return table;
    }

    @Override
    public String toString() {
        return format("JOIN (%s)", table.getSqlTable());
    }
}

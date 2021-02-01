package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Field;
import com.heliorm.def.Join;
import com.heliorm.def.OnClause;

import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class JoinPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Part<LT, LO, RT, RO> implements Join<LT, LO, RT, RO> {

    private final Table table;

    public JoinPart(Part left, RT table) {
        super(Type.JOIN, left);
        this.table = table;
    }

    @Override
    public <L extends Field<LT, LO, C>, R extends Field<RT, RO, C>, C> OnClause<LT, LO, RT, RO> on(L leftField, R rightField) {
        return new OnClausePart(this, leftField, rightField);
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

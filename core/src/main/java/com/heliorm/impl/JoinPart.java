package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Join;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author gideon
 */
public class JoinPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> implements Join<LT, LO> {

    private final RT table;
    private final OnPart<LT, LO, RT, RO> on;
    private final Optional<WherePart<RT, RO>> where;
    private final List<JoinPart<?, ?, ?, ?>> joins;

    public JoinPart(RT table, OnPart<LT, LO, RT, RO> on, Optional<WherePart<RT, RO>> where, List<JoinPart<RT, RO, ?, ?>> joins) {
        this.table = table;
        this.on = on;
        this.where = where;
        this.joins = new ArrayList<>(joins);
    }

    public RT getTable() {
        return table;
    }

    public OnPart<LT, LO, RT, RO> getOn() {
        return on;
    }

    public Optional<WherePart<RT, RO>> getWhere() {
        return where;
    }

    public List<JoinPart<?, ?, ?, ?>> getJoins() {
        return joins;
    }
}

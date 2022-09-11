package com.heliorm.impl;

import com.heliorm.Table;
import com.heliorm.def.Join;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author gideon
 */
public class JoinPart<LO, RO> implements Join<LO> {

    private final Table<RO> table;
    private final OnPart<LO, RO> on;
    private final Optional<WherePart<RO>> where;
    private final List<JoinPart<?, ?>> joins;

    public JoinPart(Table<RO> table, OnPart<LO, RO> on, Optional<WherePart<RO>> where, List<JoinPart<RO, ?>> joins) {
        this.table = table;
        this.on = on;
        this.where = where;
        this.joins = new ArrayList<>(joins);
    }

    public Table<RO> getTable() {
        return table;
    }

    public OnPart< LO,  RO> getOn() {
        return on;
    }

    public Optional<WherePart<RO>> getWhere() {
        return where;
    }

    public List<JoinPart<?, ?>> getJoins() {
        return joins;
    }
}

package com.heliorm;

import com.heliorm.def.Continuation;
import com.heliorm.def.Join;
import com.heliorm.def.On;
import com.heliorm.def.Where;
import com.heliorm.impl.JoinPart;
import com.heliorm.impl.OnPart;
import com.heliorm.impl.WherePart;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Static methods for building queries.
 *
 * @author gideon
 */
public final class Query {

    private Query() {
    }

    public static <O> Where<O> where(Continuation<O> expr) {
        return new WherePart<>(expr);
    }

    public static < LO,  RO> Join<LO> join(Table<RO> table, On<LO, RO> on) {
        return new JoinPart(table, (OnPart) on, Optional.empty(), Collections.EMPTY_LIST);
    }

    public static < LO,  RO> Join<LO> join(Table<RO> table, On<LO, RO> on, Where<RO> where) {
        return new JoinPart(table, (OnPart) on, Optional.of((WherePart) where), Collections.EMPTY_LIST);
    }

    public static < LO,  RO> Join<LO> join(Table<RO> table, On<LO, RO> on, Join<RO>... joins) {
        return new JoinPart(table, (OnPart) on, Optional.empty(), Arrays.asList(joins));
    }

    public static < LO,  RO> Join<LO> join(Table<RO> table, On<LO, RO> on, Where<RO> where, Join<RO>... joins) {
        return new JoinPart(table, (OnPart) on, Optional.of((WherePart) where), Arrays.asList(joins));
    }

    public static < LO,  RO, C> On<LO, RO> on(Field<LO, C> leftField, Field<RO, C> rightField) {
        return new OnPart(leftField, rightField);
    }
}

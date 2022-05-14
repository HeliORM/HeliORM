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
public class Query {


    private Query() {
    }

    public static <F extends Field<T, O, C>, T extends Table<O>, O, C> Where<T, O> where(Continuation<T, O> expr) {
        return new WherePart(expr);
    }

    public static <LT extends Table<LO>, LO, RT extends Table<RO>, RO> Join<LT, LO> join(RT table, On<LT, LO, RT, RO> on) {
        return new JoinPart(table, (OnPart) on, Optional.empty(), Collections.EMPTY_LIST);
    }

    public static <LT extends Table<LO>, LO, RT extends Table<RO>, RO> Join<LT, LO> join(RT table, On<LT, LO, RT, RO> on, Where<RT, RO> where) {
        return new JoinPart(table, (OnPart) on, Optional.of((WherePart) where), Collections.EMPTY_LIST);
    }

    public static <LT extends Table<LO>, LO, RT extends Table<RO>, RO> Join<LT, LO> join(RT table, On<LT, LO, RT, RO> on, Join<RT, RO>... joins) {
        return new JoinPart(table, (OnPart) on, Optional.empty(), Arrays.asList(joins));
    }

    public static <LT extends Table<LO>, LO, RT extends Table<RO>, RO> Join<LT, LO> join(RT table, On<LT, LO, RT, RO> on, Where<RT, RO> where, Join<RT, RO>... joins) {
        return new JoinPart(table, (OnPart) on, Optional.of((WherePart) where), Arrays.asList(joins));
    }

    public static <LT extends Table<LO>, LO, RT extends Table<RO>, RO, C> On<LT, LO, RT, RO> on(Field<LT, LO, C> leftField, Field<RT, RO, C> rightField) {
        return new OnPart(leftField, rightField);
    }
}

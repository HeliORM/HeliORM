package com.heliorm.def;

import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public interface OnClause<DT extends Table<DO>, DO, LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Select<DT, DO, RT, RO> {

    <NT extends Table<NO>, NO> Join<DT,DO,RT,RO,NT,NO> thenJoin(NT table);

}

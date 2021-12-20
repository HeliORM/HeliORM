package com.heliorm.def;

import com.heliorm.Table;

public interface OnContinuation <DT extends Table<DO>, DO, LT extends Table<LO>, LO> extends Continuation<DT, DO, LT, LO> {

    <NT extends Table<NO>, NO> Join<DT,DO,LT,LO,NT,NO> thenJoin(NT table);

}

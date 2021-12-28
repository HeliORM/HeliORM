package com.heliorm.def;

import com.heliorm.Table;

public interface Where<T extends Table<O>, O> {

    Where<T,O> and(Continuation<T, O> cont);

    Where<T,O> or(Continuation<T, O> cont);

}

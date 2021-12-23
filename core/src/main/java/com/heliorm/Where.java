package com.heliorm;

import com.heliorm.def.Continuation;

public interface Where<T extends Table<O>, O> {

    Where<T,O> and(Continuation<T, O> cont);

    Where<T,O> or(Continuation<T, O> cont);

}

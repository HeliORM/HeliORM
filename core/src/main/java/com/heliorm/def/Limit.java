package com.heliorm.def;

public interface Limit<O> {

    Executable<O> limit(int from, int number);

    Executable<O> limit(int from);

}

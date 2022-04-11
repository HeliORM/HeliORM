package com.heliorm.impl;

public class LimitPart<O>  {

    private final int from;
    private final int number;


    public LimitPart(int from, int number) {
        this.from = from;
        this.number = number;
    }

    public LimitPart(int number) {
        this(0, number);
    }

    public int getFrom() {
        return from;
    }

    public int getNumber() {
        return number;
    }
}

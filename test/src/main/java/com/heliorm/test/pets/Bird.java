package com.heliorm.test.pets;

import com.heliorm.annotation.Pojo;

/**
 * @author gideon
 */
@Pojo
public class Bird extends Avian {

    private Type type;
    private int singTime;

    public int getSingTime() {
        return singTime;
    }

    public void setSingTime(int singTime) {
        this.singTime = singTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        CAGED, FREERANGE
    }
}

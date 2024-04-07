package com.heliorm;

public interface TypeAdapter<T> {

    Class<T> javaType();

    T fromSql(String sqlValue);

    String toSql(T javaValue);


}

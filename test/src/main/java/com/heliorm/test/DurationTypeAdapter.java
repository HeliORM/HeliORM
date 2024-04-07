package com.heliorm.test;

import com.heliorm.TypeAdapter;

import java.time.Duration;

public final class DurationTypeAdapter implements TypeAdapter<Duration> {
    @Override
    public Class<Duration> javaType() {
        return Duration.class;
    }

    @Override
    public Duration fromSql(String sqlValue) {
        return Duration.parse(sqlValue);
    }

    @Override
    public String toSql(Duration javaValue) {
        return javaValue.toString();
    }
}

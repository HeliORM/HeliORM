package com.heliorm.test.place;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

@Pojo
public record Province(@PrimaryKey Long provinceId, @Column(length = 30) String name, Type type) {

    public enum Type {
        DISTOPIAN_HELLHOLE, COOL_PLACE
    }
}

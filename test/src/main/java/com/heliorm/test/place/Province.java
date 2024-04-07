package com.heliorm.test.place;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

@Pojo
public final class Province {

    @PrimaryKey
    private Long provinceId;
    @Column(length = 30)
    private String name;

    public Province(Long provinceId, String name) {
        this.provinceId = provinceId;
        this.name = name;
    }

    public Long provinceId() {
        return provinceId;
    }

    public Province() {
    }

    public String name() {
        return name;
    }
}

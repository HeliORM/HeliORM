package com.heliorm.test.place;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

import java.time.LocalDateTime;

@Pojo
public class Town {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    @Column(length = 30, nullable = false)
    private String name;
    @ForeignKey(pojo = Province.class)
    private Long provinceId;
    private LocalDateTime founded;
    private boolean hasPetshop;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public void setFounded(LocalDateTime founded) {
        this.founded = founded;
    }
}

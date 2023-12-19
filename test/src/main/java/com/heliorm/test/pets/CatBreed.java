package com.heliorm.test.pets;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

/**
 * @author gideon
 */
@Pojo
public class CatBreed {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    @Column(length = 32, nullable = false)
    private String name;

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
}

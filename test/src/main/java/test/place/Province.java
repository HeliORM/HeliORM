package test.place;

import com.heliorm.annotation.Collection;
import com.heliorm.annotation.Column;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

import java.util.Set;

@Pojo
public class Province {

    @PrimaryKey
    private Long provinceId;
    @Column(length = 30)
    private String name;
    @Collection(pojo = Town.class)
    private Set<Town> towns;

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Town> getTowns() {
        return towns;
    }

    public void setTowns(Set<Town> towns) {
        this.towns = towns;
    }
}

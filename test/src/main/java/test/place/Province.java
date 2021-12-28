package test.place;

import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;
import com.heliorm.annotation.Text;

@Pojo
public class Province {

    @PrimaryKey
    private Long provinceId;
    @Text(length = 30)
    private String name;

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
}

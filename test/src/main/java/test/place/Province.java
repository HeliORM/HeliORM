package test.place;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

@Pojo
public interface Province {

    @PrimaryKey
    Long provinceId();

    @Column(length = 30)
    String name();

}

package test.place;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

@Pojo
public interface Town {

    @PrimaryKey()
    Long id();

    @Column(length = 30, nullable = false)
    String name();

    @ForeignKey(pojo = Province.class)
    Long provinceId();

}

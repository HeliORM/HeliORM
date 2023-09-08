package test.pets;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;

/**
 * @author gideon
 */
@Pojo
public interface CatBreed {

    @PrimaryKey()
    Long id();

    @Column(length = 32)
    String name();
}

package test.pets;

import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Pojo;

/**
 * @author gideon
 */
@Pojo
public interface Cat extends Mamal {

    CatType type();

    @ForeignKey(pojo = CatBreed.class)
    Long breedId();

}

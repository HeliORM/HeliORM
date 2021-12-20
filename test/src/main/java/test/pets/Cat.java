package test.pets;

import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Pojo;

/**
 * @author gideon
 */
@Pojo
public class Cat extends Mamal {

    private CatType type;
    @ForeignKey(pojo = CatBreed.class)
    private Long breedId;

    public CatType getType() {
        return type;
    }

    public void setType(CatType type) {
        this.type = type;
    }

    public Long getBreedId() {
        return breedId;
    }

    public void setBreedId(Long breedId) {
        this.breedId = breedId;
    }
}

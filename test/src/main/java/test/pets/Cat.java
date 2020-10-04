package test.pets;

import com.heliorm.annotation.Pojo;

/**
 * @author gideon
 */
@Pojo
public class Cat extends Mamal {


    private CatType type;

    public CatType getType() {
        return type;
    }

    public void setType(CatType type) {
        this.type = type;
    }
}

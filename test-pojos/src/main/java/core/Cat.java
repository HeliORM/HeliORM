package core;

import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Cat extends Pet {

    @PrimaryKey
    private Long catNumber;

    public Long getCatNumber() {
        return catNumber;
    }

    public void setCatNumber(Long catNumber) {
        this.catNumber = catNumber;
    }

}

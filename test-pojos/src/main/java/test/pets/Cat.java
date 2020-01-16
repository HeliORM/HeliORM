package test.pets;

import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Cat extends Mamal {

    public enum Type {
        INDOOR, OUTDOOR;
    }

    @PrimaryKey
    private Long catNumber;

    private Type type;

    public Long getCatNumber() {
        return catNumber;
    }

    public void setCatNumber(Long catNumber) {
        this.catNumber = catNumber;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }



}

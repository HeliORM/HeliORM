package test.pets;

import com.heliorm.annotation.Pojo;

/**
 * @author gideon
 */
@Pojo
public class Bird extends Avian {

    public enum Type {
        CAGED, FREERANGE;
    }
    private Type type;

    private Integer singTime;

    public Integer getSingTime() {
        return singTime;
    }

    public void setSingTime(Integer singTime) {
        this.singTime = singTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

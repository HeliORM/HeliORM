package test.pets;

import com.heliorm.annotation.Pojo;

import java.time.Duration;

/**
 * @author gideon
 */
@Pojo
public class Bird extends Avian {

    private Type type;
    private Duration singTime;

    public Duration getSingTime() {
        return singTime;
    }

    public void setSingTime(Duration singTime) {
        this.singTime = singTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        CAGED, FREERANGE;
    }
}

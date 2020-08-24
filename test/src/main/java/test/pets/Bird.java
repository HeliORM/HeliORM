package test.pets;

import com.heliorm.annotation.Pojo;

import java.time.Duration;

/**
 * @author gideon
 */
@Pojo
public class Bird extends Avian {

    public enum Kind {
        CAGED, FREERANGE;
    }
    private Kind kind;

    private Duration singTime;
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Duration getSingTime() {
        return singTime;
    }

    public void setSingTime(Duration singTime) {
        this.singTime = singTime;
    }

}

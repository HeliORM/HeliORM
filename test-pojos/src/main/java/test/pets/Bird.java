package test.pets;

import net.legrange.orm.annotation.Pojo;

import java.time.Duration;

/**
 * @author gideon
 */
@Pojo
public class Bird extends Avian {

    public enum Kind {
        CAGED, FREERANGE;
    }
//
//    @PrimaryKey(autoIncrement = true)
//    private String birdId;

    private Kind kind;

    private Duration singTime;
//
//    public String getBirdId() {
//        return birdId;
//    }
//
//    public void setBirdId(String birdId) {
//        this.birdId = birdId;
//    }

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

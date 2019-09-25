package test.pets;

import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Bird extends Pet {

    public enum Kind {
        CAGED, FREERANGE;
    }

    @PrimaryKey(autoIncrement = true)
    private String birdId;

    private Kind kind;

    public String getBirdId() {
        return birdId;
    }

    public void setBirdId(String birdId) {
        this.birdId = birdId;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

}

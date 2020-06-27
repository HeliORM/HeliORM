package test.pets;

import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.annotation.PrimaryKey;

@Pojo
public class Fish  {

    @PrimaryKey(autoIncrement = true)
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package core;

import me.legrange.orm.annotation.ForeignKey;
import me.legrange.orm.annotation.Pojo;
import pojos.Obj;

/**
 *
 * @author gideon
 */
@Pojo
public abstract class Pet extends Obj {

    @ForeignKey(pojo = Person.class)
    private Long personNumber;
    private String name;
    private int age;

    @Override
    public String toString() {
        return "" + getClass().getSimpleName() + "{" + "name=" + name + ", age=" + age + '}';
    }

}

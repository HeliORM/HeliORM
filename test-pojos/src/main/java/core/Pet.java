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

    public Long getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(Long personNumber) {
        this.personNumber = personNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "" + getClass().getSimpleName() + "{" + "name=" + name + ", age=" + age + '}';
    }

}

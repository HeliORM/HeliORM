package test.pets;

import net.legrange.orm.annotation.ForeignKey;
import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.annotation.PrimaryKey;
import pojos.Obj;
import test.persons.Person;

/**
 * @author gideon
 */
@Pojo
public abstract class Pet extends Obj {

    @PrimaryKey(autoIncrement = true)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

package com.heliorm.test.pets;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Index;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;
import com.heliorm.test.persons.Person;

import java.util.Date;

/**
 * @author gideon
 */
@Pojo
@Index(columns = {"name", "age"}, unique = false)
@Index(columns = {"personId"}, unique = false)
public abstract class Pet {

    @PrimaryKey(autoIncrement = true)
    private Long id;

    @ForeignKey(pojo = Person.class)
    private Long personId;
    @Column(length = 32)
    private String name;
    private int age;
    private Date birthday;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}

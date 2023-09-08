package test.pets;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Index;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;
import test.persons.Person;

import java.util.Date;

/**
 * @author gideon
 */
@Pojo
@Index(columns = {"name", "age"}, unique = false)
@Index(columns = {"personId"}, unique = false)
public interface Pet {

    @PrimaryKey(autoIncrement = true)
    Long id();

    @ForeignKey(pojo = Person.class)
    Long personId();

    @Column(length = 32)
    String name();

    int age();
    Date birthday();

}

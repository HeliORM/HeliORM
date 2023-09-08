package test.persons;

import com.heliorm.annotation.Column;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;
import test.place.Town;

/**
 * @author gideon
 */
@Pojo
public interface Person {


    @PrimaryKey
    Long id();

    @ForeignKey(pojo = Town.class)
    Long townId();

    @Column
    String firstName();

    @Column(nullable = true)
    String lastName();

    @Column
    String emailAddress();

    @Column(nullable = true)
    Double income();


}

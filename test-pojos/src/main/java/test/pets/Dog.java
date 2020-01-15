package test.pets;

import net.legrange.orm.annotation.Pojo;
import net.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Dog extends Mamal {

    @PrimaryKey
    private Long dogNumber;

}
